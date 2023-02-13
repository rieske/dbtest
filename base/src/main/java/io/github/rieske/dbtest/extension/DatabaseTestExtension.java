package io.github.rieske.dbtest.extension;

import org.junit.jupiter.api.extension.Extension;

import javax.sql.DataSource;
import java.util.UUID;

public abstract class DatabaseTestExtension implements Extension {
    protected final String databaseName = "testdb_" + UUID.randomUUID().toString().replace('-', '_');

    public abstract DataSource getDataSource();

    abstract protected void migrateDatabase(DataSource dataSource);

    protected abstract void createFreshMigratedDatabase();
}
