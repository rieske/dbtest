package io.github.rieske.dbtest.extension;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

abstract class DatabaseStateStrategy {
    protected final TestDatabase database;
    protected final Consumer<DataSource> migrator;
    protected final Runnable databaseCreator;

    DatabaseStateStrategy(TestDatabase database, Consumer<DataSource> migrator, boolean migrateOnce) {
        this.database = database;
        this.migrator = migrator;
        if (migrateOnce) {
            this.databaseCreator = () -> database.cloneTemplateDatabaseTo(getDatabaseName());
            database.migrateTemplateDatabase(migrator);
        } else {
            this.databaseCreator = () -> {
                database.createDatabase(getDatabaseName());
                migrator.accept(getDataSource());
            };
        }
    }

    protected abstract void prepareTestDatabase();

    protected void beforeTest(Class<?> testClass) {
    }

    protected void afterTest() {
    }

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

    PerMethodStrategy(TestDatabase database, Consumer<DataSource> migrator, boolean migrateOnce) {
        super(database, migrator, migrateOnce);
    }

    @Override
    protected void prepareTestDatabase() {
        databaseCreator.run();
    }

    @Override
    protected void afterTest() {
        database.dropDatabase(databaseName);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
}

class PerClassStrategy extends DatabaseStateStrategy {
    private static final Map<Class<?>, String> CLASS_DATABASES = new ConcurrentHashMap<>();
    private Class<?> testClass;
    private String databaseName;

    PerClassStrategy(TestDatabase database, Consumer<DataSource> migrator, boolean migrateOnce) {
        super(database, migrator, migrateOnce);
    }

    @Override
    protected void prepareTestDatabase() {
        databaseName = CLASS_DATABASES.get(testClass);
        if (databaseName == null) {
            synchronized (CLASS_DATABASES) {
                databaseName = CLASS_DATABASES.get(testClass);
                if (databaseName == null) {
                    databaseName = newDatabaseName();
                    databaseCreator.run();
                    CLASS_DATABASES.put(testClass, databaseName);
                }
            }
        }
    }

    @Override
    protected void beforeTest(Class<?> testClass) {
        this.testClass = testClass;
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
}

class PerExecutionStrategy extends DatabaseStateStrategy {
    private static final String DATABASE_NAME = newDatabaseName();
    private static volatile boolean databaseCreated = false;

    PerExecutionStrategy(TestDatabase database, Consumer<DataSource> migrator, boolean migrateOnce) {
        super(database, migrator, migrateOnce);
    }

    @Override
    protected void prepareTestDatabase() {
        createDatabase(databaseCreator);
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
