package io.github.rieske.dbtest.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Base class for concrete database test extension implementations.
 * Encapsulates the core extension behavior that does not rely on backing database specifics.
 */
public abstract class DatabaseTestExtension implements Extension, BeforeEachCallback, AfterEachCallback, InvocationInterceptor {
    private static final Logger log = LoggerFactory.getLogger(DatabaseTestExtension.class);

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

    private final DatabaseState databaseState;
    private final BiConsumer<DatabaseEngine, String> databaseCreator;

    private Class<?> testClass;
    private String databaseName;

    DatabaseTestExtension(TestDatabase database, Mode dataRetentionMode, boolean migrateOnce) {
        this.databaseState = database.getState(dataRetentionMode);
        this.databaseCreator = makeDatabaseCreator(migrateOnce, this::migrateDatabase);
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
        databaseState.afterTestMethod(databaseName);
    }

    /**
     * Get the data source to be used in the tests against the database.
     * Use this method to inject a data source to any units under test that interact with the database.
     *
     * @return dataSource for a migrated database
     */
    public DataSource getDataSource() {
        this.databaseName = databaseState.ensureDatabaseCreated(databaseName, testClass, databaseCreator);
        return databaseState.dataSourceForDatabase(databaseName);
    }

    /**
     * Implement this method to apply migrations to the test database.
     * The extension will ensure that the database exposed to the testing code is migrated.
     *
     * @param dataSource to use with database migration tool of your choice
     */
    abstract protected void migrateDatabase(DataSource dataSource);

    private static BiConsumer<DatabaseEngine, String> makeDatabaseCreator(boolean migrateOnce, Consumer<DataSource> migrator) {
        if (migrateOnce) {
            return (database, databaseName) -> {
                database.ensureTemplateDatabaseMigrated(migrator);
                long startTime = System.currentTimeMillis();
                log.info("Copying migrated template database to {}", databaseName);
                database.cloneTemplateDatabaseTo(databaseName);
                log.info("Copied migrated template database to {} in {}", databaseName, TimeUtils.durationSince(startTime));
            };
        } else {
            return (database, databaseName) -> {
                database.createDatabase(databaseName);
                long startTime = System.currentTimeMillis();
                log.info("Migrating database {}", databaseName);
                migrator.accept(database.dataSourceForDatabase(databaseName));
                log.info("Migrated database {} in {}", databaseName, TimeUtils.durationSince(startTime));
            };
        }
    }
}
