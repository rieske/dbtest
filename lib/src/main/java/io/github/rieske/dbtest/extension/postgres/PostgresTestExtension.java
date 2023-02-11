package io.github.rieske.dbtest.extension.postgres;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;

public abstract class PostgresTestExtension extends DatabaseTestExtension implements BeforeEachCallback, AfterEachCallback {

    protected final PostgresTestDatabase database;

    PostgresTestExtension(String databaseVersion) {
        this.database = PostgresTestDatabaseManager.getDatabase(databaseVersion);
    }

    abstract protected void migrateDatabase(DataSource dataSource);

    @Override
    public void beforeEach(ExtensionContext context) {
        createFreshMigratedDatabase();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        database.dropDatabase(databaseName);
    }

    @Override
    public DataSource getDataSource() {
        return database.dataSourceForDatabase(databaseName);
    }
}
