package io.github.rieske.dbtest.extension.postgres;

import io.github.rieske.dbtest.DatabaseTestExtension;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;
import java.util.UUID;

public abstract class PostgresTestExtension extends DatabaseTestExtension implements BeforeEachCallback, AfterEachCallback {

    protected static final PostgresTestDatabase database = new PostgresTestDatabase();

    protected final String databaseName = "testdb_" + UUID.randomUUID().toString().replace('-', '_');

    abstract protected void migrateDatabase(DataSource dataSource);

    @Override
    public void beforeEach(ExtensionContext context) {
        createFreshMigratedDatabase();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        database.executeInPostgresSchema("DROP DATABASE " + databaseName);
    }

    @Override
    public DataSource getDataSource() {
        return database.dataSourceForDatabase(databaseName);
    }
}
