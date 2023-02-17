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

    protected abstract String getDatabaseName();

    DataSource getDataSource() {
        return database.dataSourceForDatabase(getDatabaseName());
    }

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
    protected String getDatabaseName() {
        return databaseName;
    }
}

class PerClassStrategy extends DatabaseStateStrategy {
    private static class DatabaseState {
        final String name;
        final boolean created;

        DatabaseState(String name, boolean created) {
            this.name = name;
            this.created = created;
        }

        static DatabaseState create() {
            return new DatabaseState(newDatabaseName(), false);
        }
    }

    private static final Map<Class<?>, DatabaseState> CLASS_DATABASE_STATES = new ConcurrentHashMap<>();
    private Class<?> testClass;
    private String databaseName;

    PerClassStrategy(TestDatabase database) {
        super(database);
    }

    @Override
    protected void beforeTest(Class<?> testClass) {
        this.testClass = testClass;
        this.databaseName = CLASS_DATABASE_STATES.computeIfAbsent(testClass, k -> DatabaseState.create()).name;
    }

    @Override
    void cloneTemplateDatabaseToTestDatabase() {
        createDatabase(() -> database.cloneTemplateDatabaseTo(databaseName));
    }

    @Override
    void createAndMigrateDatabase(Consumer<DataSource> migrator) {
        createDatabase(() -> {
            database.createDatabase(databaseName);
            migrator.accept(getDataSource());
        });
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    private void createDatabase(Runnable action) {
        if (!CLASS_DATABASE_STATES.get(testClass).created) {
            synchronized (CLASS_DATABASE_STATES) {
                if (!CLASS_DATABASE_STATES.get(testClass).created) {
                    action.run();
                    CLASS_DATABASE_STATES.put(testClass, new DatabaseState(databaseName, true));
                }
            }
        }
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

    @Override
    protected String getDatabaseName() {
        return DATABASE_NAME;
    }

    private static synchronized void createDatabase(Runnable action) {
        if (!databaseCreated) {
            action.run();
            databaseCreated = true;
        }
    }
}
