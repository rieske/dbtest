package io.github.rieske.dbtest.extension.postgresql;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;

abstract class PostgreSQLTestExtension extends DatabaseTestExtension implements BeforeEachCallback, AfterEachCallback {

    protected final PostgreSQLTestDatabase database;

    PostgreSQLTestExtension(String databaseVersion) {
        this.database = PostgreSQLTestDatabaseManager.getDatabase(databaseVersion);
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
