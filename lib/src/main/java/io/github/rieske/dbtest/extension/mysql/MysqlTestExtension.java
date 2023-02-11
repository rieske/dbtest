package io.github.rieske.dbtest.extension.mysql;

import io.github.rieske.dbtest.DatabaseTestExtension;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;

public abstract class MysqlTestExtension extends DatabaseTestExtension implements BeforeEachCallback, AfterEachCallback {
    protected static final MysqlTestDatabase database = new MysqlTestDatabase();

    abstract protected void migrateDatabase(DataSource dataSource);

    @Override
    public void beforeEach(ExtensionContext context) {
        createFreshMigratedDatabase();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        database.executePrivileged("DROP DATABASE " + databaseName);
    }

    @Override
    public DataSource getDataSource() {
        return database.dataSourceForDatabase(databaseName);
    }
}
