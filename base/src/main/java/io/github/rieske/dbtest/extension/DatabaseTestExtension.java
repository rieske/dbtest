package io.github.rieske.dbtest.extension;

import org.junit.jupiter.api.extension.Extension;

import javax.sql.DataSource;
import java.util.UUID;

public abstract class DatabaseTestExtension implements Extension {
    protected final String databaseName = "testdb_" + UUID.randomUUID().toString().replace('-', '_');

    /**
     * Get the data source to be used in the tests against the database.
     * Use this method to inject a data source to any units under test that interact with the database.
     *
     * @return dataSource for a migrated database
     */
    public abstract DataSource getDataSource();

    abstract protected void migrateDatabase(DataSource dataSource);

    protected abstract void createFreshMigratedDatabase();
}
