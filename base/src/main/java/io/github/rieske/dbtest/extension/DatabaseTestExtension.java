package io.github.rieske.dbtest.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;
import java.util.function.Consumer;

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
        DATABASE_PER_EXECUTION;

        private String ensureDatabaseCreated(TestDatabase database, Consumer<String> databaseCreator, Class<?> testClass) {
            switch (this) {
                case DATABASE_PER_TEST_METHOD:
                    return database.perMethod.ensureDatabaseCreated(testClass, databaseCreator);
                case DATABASE_PER_TEST_CLASS:
                    return database.perClass.ensureDatabaseCreated(testClass, databaseCreator);
                case DATABASE_PER_EXECUTION:
                    return database.perExecution.ensureDatabaseCreated(testClass, databaseCreator);
                default:
                    throw new IllegalStateException("No strategy exists for " + this + " mode");
            }
        }
    }

    private final TestDatabase database;
    private final Consumer<String> databaseCreator;
    private final Mode mode;

    private String databaseName;

    DatabaseTestExtension(TestDatabase database, Mode mode, boolean migrateOnce) {
        this.database = database;
        this.databaseCreator = makeDatabaseCreator(database, migrateOnce);
        this.mode = mode;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        this.databaseName = mode.ensureDatabaseCreated(database, databaseCreator, context.getRequiredTestClass());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (mode == Mode.DATABASE_PER_TEST_METHOD) {
            database.dropDatabase(databaseName);
        }
    }

    /**
     * Get the data source to be used in the tests against the database.
     * Use this method to inject a data source to any units under test that interact with the database.
     *
     * @return dataSource for a migrated database
     */
    public DataSource getDataSource() {
        return database.dataSourceForDatabase(databaseName);
    }

    /**
     * Implement this method to apply migrations to the test database.
     * The extension will ensure that the database exposed to the testing code is migrated.
     *
     * @param dataSource to use with database migration tool of your choice
     */
    abstract protected void migrateDatabase(DataSource dataSource);

    private Consumer<String> makeDatabaseCreator(TestDatabase database, boolean migrateOnce) {
        if (migrateOnce) {
            return databaseName -> {
                database.ensureTemplateDatabaseMigrated(this::migrateDatabase);
                database.cloneTemplateDatabaseTo(databaseName);
            };
        } else {
            return databaseName -> {
                database.createDatabase(databaseName);
                migrateDatabase(database.dataSourceForDatabase(databaseName));
            };
        }
    }
}
