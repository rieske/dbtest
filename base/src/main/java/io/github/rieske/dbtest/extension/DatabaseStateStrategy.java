package io.github.rieske.dbtest.extension;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

abstract class DatabaseStateStrategy {
    protected final TestDatabase database;

    DatabaseStateStrategy(TestDatabase database) {
        this.database = database;
    }

    protected void beforeTest(Class<?> testClass) {
    }

    protected void afterTest() {
    }

    abstract void cloneTemplateDatabaseToTestDatabase();

    abstract void createAndMigrateDatabase(Consumer<DataSource> migrator);

    abstract DataSource getDataSource();

    protected static String newDatabaseName() {
        return "testdb_" + UUID.randomUUID().toString().replace('-', '_');
    }
}

class PerMethodStrategy extends DatabaseStateStrategy {
    private final String databaseName = newDatabaseName();

    PerMethodStrategy(TestDatabase database) {
        super(database);
    }

    @Override
    protected void afterTest() {
        database.dropDatabase(databaseName);
    }

    @Override
    void cloneTemplateDatabaseToTestDatabase() {
        database.cloneTemplateDatabaseTo(databaseName);
    }

    @Override
    void createAndMigrateDatabase(Consumer<DataSource> migrator) {
        database.createDatabase(databaseName);
        migrator.accept(getDataSource());
    }

    @Override
    DataSource getDataSource() {
        return database.dataSourceForDatabase(databaseName);
    }
}

class PerClassStrategy extends DatabaseStateStrategy {
    private static class DatabaseState {
        final Class<?> key;
        final String name;
        final boolean created;

        DatabaseState(Class<?> key, String name, boolean created) {
            this.key = key;
            this.name = name;
            this.created = created;
        }

        static DatabaseState create(Class<?> key) {
            return new DatabaseState(key, newDatabaseName(), false);
        }
    }

    private static final Map<Class<?>, DatabaseState> CLASS_DATABASE_STATES = new ConcurrentHashMap<>();
    private volatile DatabaseState databaseState;

    PerClassStrategy(TestDatabase database) {
        super(database);
    }

    @Override
    protected void beforeTest(Class<?> testClass) {
        this.databaseState = CLASS_DATABASE_STATES.computeIfAbsent(testClass, DatabaseState::create);
    }

    @Override
    void cloneTemplateDatabaseToTestDatabase() {
        if (!CLASS_DATABASE_STATES.get(databaseState.key).created) {
            synchronized (CLASS_DATABASE_STATES) {
                if (CLASS_DATABASE_STATES.get(databaseState.key).created) {
                    return;
                }
                database.cloneTemplateDatabaseTo(databaseState.name);
                this.databaseState = new DatabaseState(databaseState.key, databaseState.name, true);
                CLASS_DATABASE_STATES.put(databaseState.key, this.databaseState);
            }
        }
    }

    @Override
    void createAndMigrateDatabase(Consumer<DataSource> migrator) {
        if (!CLASS_DATABASE_STATES.get(databaseState.key).created) {
            synchronized (CLASS_DATABASE_STATES) {
                if (CLASS_DATABASE_STATES.get(databaseState.key).created) {
                    return;
                }
                database.createDatabase(databaseState.name);
                migrator.accept(getDataSource());
                this.databaseState = new DatabaseState(databaseState.key, databaseState.name, true);
                CLASS_DATABASE_STATES.put(databaseState.key, this.databaseState);
            }
        }
    }

    @Override
    DataSource getDataSource() {
        return database.dataSourceForDatabase(databaseState.name);
    }
}

class PerExecutionStrategy extends DatabaseStateStrategy {
    private static final String DATABASE_NAME = newDatabaseName();
    private static volatile boolean databaseCreated = false;

    PerExecutionStrategy(TestDatabase database) {
        super(database);
    }

    @Override
    void cloneTemplateDatabaseToTestDatabase() {
        if (!databaseCreated) {
            createDatabase(() -> database.cloneTemplateDatabaseTo(DATABASE_NAME));
        }
    }

    @Override
    void createAndMigrateDatabase(Consumer<DataSource> migrator) {
        if (!databaseCreated) {
            createDatabase(() -> {
                database.createDatabase(DATABASE_NAME);
                migrator.accept(getDataSource());
            });
        }
    }

    private static synchronized void createDatabase(Runnable action) {
        if (!databaseCreated) {
            action.run();
            databaseCreated = true;
        }
    }

    @Override
    DataSource getDataSource() {
        return database.dataSourceForDatabase(DATABASE_NAME);
    }
}
