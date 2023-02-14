package io.github.rieske.dbtest.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;
import java.util.UUID;

public abstract class DatabaseTestExtension implements Extension, BeforeEachCallback, AfterEachCallback {
    protected final TestDatabase database;
    protected final String databaseName = "testdb_" + UUID.randomUUID().toString().replace('-', '_');

    protected DatabaseTestExtension(TestDatabase database) {
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

    abstract protected void migrateDatabase(DataSource dataSource);

    protected abstract void createFreshMigratedDatabase();
}
