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

    abstract void beforeTest(Class<?> testClass);

    abstract void afterTest();

    abstract void cloneTemplateDatabaseToTestDatabase();

    abstract void createAndMigrateDatabase(Consumer<DataSource> migrator);

    abstract DataSource getDataSource();
}

class PerMethodStrategy extends DatabaseStateStrategy {
    private DatabaseState databaseState;

    PerMethodStrategy(TestDatabase database) {
        super(database);
    }

    @Override
    void beforeTest(Class<?> testClass) {
        this.databaseState = DatabaseState.create(null);
    }

    @Override
    void afterTest() {
        database.dropDatabase(databaseState.name);
    }

    @Override
    void cloneTemplateDatabaseToTestDatabase() {
        database.cloneTemplateDatabaseTo(databaseState.name);
    }

    @Override
    void createAndMigrateDatabase(Consumer<DataSource> migrator) {
        database.createDatabase(databaseState.name);
        migrator.accept(getDataSource());
    }

    @Override
    DataSource getDataSource() {
        return database.dataSourceForDatabase(databaseState.name);
    }
}

class PerClassStrategy extends DatabaseStateStrategy {
    private static final Map<Class<?>, DatabaseState> CLASS_DATABASE_STATES = new ConcurrentHashMap<>();
    private volatile DatabaseState databaseState;

    PerClassStrategy(TestDatabase database) {
        super(database);
    }

    @Override
    void beforeTest(Class<?> testClass) {
        this.databaseState = CLASS_DATABASE_STATES.computeIfAbsent(testClass, DatabaseState::create);
    }

    @Override
    void afterTest() {
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
    private static volatile DatabaseState EXECUTION_DATABASE_STATE = DatabaseState.create(null);

    PerExecutionStrategy(TestDatabase database) {
        super(database);
    }

    @Override
    void beforeTest(Class<?> testClass) {
    }

    @Override
    void afterTest() {
    }

    @Override
    void cloneTemplateDatabaseToTestDatabase() {
        if (!EXECUTION_DATABASE_STATE.created) {
            synchronized (DatabaseTestExtension.class) {
                if (EXECUTION_DATABASE_STATE.created) {
                    return;
                }
                database.cloneTemplateDatabaseTo(EXECUTION_DATABASE_STATE.name);
                EXECUTION_DATABASE_STATE = new DatabaseState(null, EXECUTION_DATABASE_STATE.name, true);
            }
        }
    }

    @Override
    void createAndMigrateDatabase(Consumer<DataSource> migrator) {
        if (!EXECUTION_DATABASE_STATE.created) {
            synchronized (DatabaseTestExtension.class) {
                if (EXECUTION_DATABASE_STATE.created) {
                    return;
                }
                database.createDatabase(EXECUTION_DATABASE_STATE.name);
                migrator.accept(getDataSource());
                EXECUTION_DATABASE_STATE = new DatabaseState(null, EXECUTION_DATABASE_STATE.name, true);
            }
        }
    }

    @Override
    DataSource getDataSource() {
        return database.dataSourceForDatabase(EXECUTION_DATABASE_STATE.name);
    }
}

class DatabaseState {
    final Class<?> key;
    final String name;
    final boolean created;

    DatabaseState(Class<?> key, String name, boolean created) {
        this.key = key;
        this.name = name;
        this.created = created;
    }

    static DatabaseState create(Class<?> key) {
        return new DatabaseState(key, "testdb_" + UUID.randomUUID().toString().replace('-', '_'), false);
    }
}
