package io.github.rieske.dbtest.extension.mysql;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;

abstract class MySQLTestExtension extends DatabaseTestExtension implements BeforeEachCallback, AfterEachCallback {
    protected final MySQLTestDatabase database;

    MySQLTestExtension(String databaseVersion) {
        this.database = MySQLTestDatabaseManager.getDatabase(databaseVersion);
    }

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
