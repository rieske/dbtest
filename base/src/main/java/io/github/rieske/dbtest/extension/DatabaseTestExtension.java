package io.github.rieske.dbtest.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;
import java.util.UUID;

/**
 * Base class for concrete database test extension implementations.
 * Encapsulates the core extension behavior that does not rely on backing database specifics.
 */
public abstract class DatabaseTestExtension implements Extension, BeforeEachCallback, AfterEachCallback {
    private final TestDatabase database;
    private final String databaseName = "testdb_" + UUID.randomUUID().toString().replace('-', '_');

    DatabaseTestExtension(TestDatabase database) {
        this.database = database;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        createFreshMigratedDatabase();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        database.dropDatabase(databaseName);
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

    abstract void createFreshMigratedDatabase();

    void migrateTemplateDatabase() {
        database.migrateTemplateDatabase(this::migrateDatabase);
    }

    void cloneTemplateDatabaseToTestDatabase() {
        database.cloneTemplateDatabaseTo(databaseName);
    }

    void createEmptyTestDatabase() {
        database.createDatabase(databaseName);
    }
}
