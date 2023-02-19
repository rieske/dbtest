package io.github.rieske.dbtest.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Base class for concrete database test extension implementations.
 * Encapsulates the core extension behavior that does not rely on backing database specifics.
 */
public abstract class DatabaseTestExtension implements Extension, BeforeEachCallback, AfterEachCallback, InvocationInterceptor {

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

        private DatabaseState getState(TestDatabase database) {
            switch (this) {
                case DATABASE_PER_TEST_METHOD:
                    return database.perMethod;
                case DATABASE_PER_TEST_CLASS:
                    return database.perClass;
                case DATABASE_PER_EXECUTION:
                    return database.perExecution;
                default:
                    throw new IllegalStateException("No database state strategy exists for " + this + " mode");
            }
        }
    }

    private final TestDatabase database;
    private final BiConsumer<TestDatabase, String> databaseCreator;
    private final Mode mode;

    private Class<?> testClass;
    private String databaseName;

    DatabaseTestExtension(TestDatabase database, Mode mode, boolean migrateOnce) {
        this.database = database;
        this.databaseCreator = makeDatabaseCreator(migrateOnce, this::migrateDatabase);
        this.mode = mode;
    }

    @Override
    public <T> T interceptTestClassConstructor(
            Invocation<T> invocation,
            ReflectiveInvocationContext<Constructor<T>> invocationContext,
            ExtensionContext extensionContext
    ) throws Throwable {
        this.testClass = extensionContext.getRequiredTestClass();
        return invocation.proceed();
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        this.testClass = context.getRequiredTestClass();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        mode.getState(database).afterTestMethod(databaseName);
    }

    /**
     * Get the data source to be used in the tests against the database.
     * Use this method to inject a data source to any units under test that interact with the database.
     *
     * @return dataSource for a migrated database
     */
    public DataSource getDataSource() {
        this.databaseName = mode.getState(database).ensureDatabaseCreated(databaseName, testClass, databaseCreator);
        return database.dataSourceForDatabase(databaseName);
    }

    /**
     * Implement this method to apply migrations to the test database.
     * The extension will ensure that the database exposed to the testing code is migrated.
     *
     * @param dataSource to use with database migration tool of your choice
     */
    abstract protected void migrateDatabase(DataSource dataSource);

    private static BiConsumer<TestDatabase, String> makeDatabaseCreator(boolean migrateOnce, Consumer<DataSource> migrator) {
        if (migrateOnce) {
            return (database, databaseName) -> {
                database.ensureTemplateDatabaseMigrated(migrator);
                database.cloneTemplateDatabaseTo(databaseName);
            };
        } else {
            return (database, databaseName) -> {
                database.createDatabase(databaseName);
                migrator.accept(database.dataSourceForDatabase(databaseName));
            };
        }
    }
}
