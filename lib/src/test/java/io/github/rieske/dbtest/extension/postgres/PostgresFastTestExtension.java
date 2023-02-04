package io.github.rieske.dbtest.extension.postgres;

import org.flywaydb.core.Flyway;

public class PostgresFastTestExtension extends PostgresTestExtension {

    static {
        Flyway.configure()
                .dataSource(dataSourceForDatabase(DB_CONTAINER.getDatabaseName()))
                .load()
                .migrate();
    }

    @Override
    void createFreshMigratedDatabase() {
        executeInPostgresSchema("CREATE DATABASE " + databaseName + " TEMPLATE " + DB_CONTAINER.getDatabaseName());
    }
}
