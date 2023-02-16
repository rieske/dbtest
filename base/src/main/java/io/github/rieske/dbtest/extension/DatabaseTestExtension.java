package io.github.rieske.dbtest.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;

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

        private DatabaseStateStrategy toStrategy(TestDatabase database) {
            switch (this) {
                case DATABASE_PER_TEST_METHOD:
                    return new PerMethodStrategy(database);
                case DATABASE_PER_TEST_CLASS:
                    return new PerClassStrategy(database);
                case DATABASE_PER_EXECUTION:
                    return new PerExecutionStrategy(database);
                default:
                    throw new IllegalStateException("No strategy exists for " + this + " mode");
            }
        }
    }

    private final TestDatabase database;
    private final DatabaseStateStrategy stateStrategy;

    DatabaseTestExtension(TestDatabase database, Mode mode) {
        this.database = database;
        this.stateStrategy = mode.toStrategy(database);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        stateStrategy.beforeTest(context.getRequiredTestClass());
        createFreshMigratedDatabase();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        stateStrategy.afterTest();
    }

    /**
     * Get the data source to be used in the tests against the database.
     * Use this method to inject a data source to any units under test that interact with the database.
     *
     * @return dataSource for a migrated database
     */
    public DataSource getDataSource() {
        return stateStrategy.getDataSource();
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
        stateStrategy.cloneTemplateDatabaseToTestDatabase();
    }

    void createAndMigrateDatabase() {
        stateStrategy.createAndMigrateDatabase(this::migrateDatabase);
    }
}
