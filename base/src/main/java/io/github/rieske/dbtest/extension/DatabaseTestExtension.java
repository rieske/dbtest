package io.github.rieske.dbtest.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class for concrete database test extension implementations.
 * Encapsulates the core extension behavior that does not rely on backing database specifics.
 */
public abstract class DatabaseTestExtension implements Extension, BeforeEachCallback, AfterEachCallback {

    /**
     * Extension execution mode. Defines the database state guarantees for test executions.
     */
    public enum Mode {
        /**
         * Create a fresh database for each test. No state in the database is shared between the tests.
         */
        DATABASE_PER_TEST_METHOD,
        /**
         * Create a fresh database for each test class. No state in the database is shared between tests in different test classes.
         */
        DATABASE_PER_TEST_CLASS,
        /**
         * Create a single database per JVM process. Any state written by tests will be visible to other tests.
         */
        DATABASE_PER_EXECUTION
    }

    private static class DatabaseState {
        final Class<?> key;
        final String name;
        final boolean created;

        private DatabaseState(Class<?> key, String name, boolean created) {
            this.key = key;
            this.name = name;
            this.created = created;
        }
    }

    private static final Map<Class<?>, DatabaseState> CLASS_DATABASE_STATES = new ConcurrentHashMap<>();
    private static volatile DatabaseState EXECUTION_DATABASE_STATE = newDatabaseState(null);
    private final Mode mode;
    private final TestDatabase database;
    private volatile DatabaseState databaseState;

    DatabaseTestExtension(TestDatabase database, Mode mode) {
        this.database = database;
        this.mode = mode;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        switch (mode) {
            case DATABASE_PER_TEST_METHOD:
                this.databaseState = newDatabaseState(null);
                break;
            case DATABASE_PER_TEST_CLASS:
                this.databaseState = CLASS_DATABASE_STATES.computeIfAbsent(context.getRequiredTestClass(), DatabaseTestExtension::newDatabaseState);
                break;
            case DATABASE_PER_EXECUTION:
                this.databaseState = EXECUTION_DATABASE_STATE;
                break;
        }
        createFreshMigratedDatabase();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (mode == Mode.DATABASE_PER_TEST_METHOD) {
            database.dropDatabase(databaseState.name);
        }
    }

    /**
     * Get the data source to be used in the tests against the database.
     * Use this method to inject a data source to any units under test that interact with the database.
     *
     * @return dataSource for a migrated database
     */
    public DataSource getDataSource() {
        return database.dataSourceForDatabase(databaseState.name);
    }

    /**
     * Implement this method to apply migrations to the test database.
     * The extension will ensure that the database exposed to the testing code is migrated.
     *
     * @param dataSource to use with database migration tool of your choice
     */
    abstract protected void migrateDatabase(DataSource dataSource);

    abstract void createFreshMigratedDatabase();

    void migrateTemplateDatabase() {
        database.migrateTemplateDatabase(this::migrateDatabase);
    }

    void cloneTemplateDatabaseToTestDatabase() {
        switch (mode) {
            case DATABASE_PER_TEST_METHOD:
                database.cloneTemplateDatabaseTo(databaseState.name);
                break;
            case DATABASE_PER_TEST_CLASS:
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
                break;
            case DATABASE_PER_EXECUTION: {
                if (!EXECUTION_DATABASE_STATE.created) {
                    synchronized (DatabaseTestExtension.class) {
                        if (EXECUTION_DATABASE_STATE.created) {
                            return;
                        }
                        database.cloneTemplateDatabaseTo(databaseState.name);
                        this.databaseState = new DatabaseState(databaseState.key, databaseState.name, true);
                        EXECUTION_DATABASE_STATE = this.databaseState;
                    }
                }
                break;
            }
        }
    }

    void createAndMigrateDatabase() {
        switch (mode) {
            case DATABASE_PER_TEST_METHOD:
                if (!databaseState.created) {
                    synchronized (this) {
                        if (databaseState.created) {
                            return;
                        }
                        database.createDatabase(databaseState.name);
                        migrateDatabase(getDataSource());
                        this.databaseState = new DatabaseState(databaseState.key, databaseState.name, true);
                    }
                }
                break;
            case DATABASE_PER_TEST_CLASS: {
                if (!CLASS_DATABASE_STATES.get(databaseState.key).created) {
                    synchronized (CLASS_DATABASE_STATES) {
                        if (CLASS_DATABASE_STATES.get(databaseState.key).created) {
                            return;
                        }
                        database.createDatabase(databaseState.name);
                        migrateDatabase(getDataSource());
                        this.databaseState = new DatabaseState(databaseState.key, databaseState.name, true);
                        CLASS_DATABASE_STATES.put(databaseState.key, this.databaseState);
                    }
                }
                break;
            }
            case DATABASE_PER_EXECUTION: {
                if (!EXECUTION_DATABASE_STATE.created) {
                    synchronized (DatabaseTestExtension.class) {
                        if (EXECUTION_DATABASE_STATE.created) {
                            return;
                        }
                        database.createDatabase(databaseState.name);
                        migrateDatabase(getDataSource());
                        this.databaseState = new DatabaseState(databaseState.key, databaseState.name, true);
                        EXECUTION_DATABASE_STATE = this.databaseState;
                    }
                }
                break;
            }
        }
    }

    private static DatabaseState newDatabaseState(Class<?> key) {
        return new DatabaseState(key, "testdb_" + UUID.randomUUID().toString().replace('-', '_'), false);
    }
}
