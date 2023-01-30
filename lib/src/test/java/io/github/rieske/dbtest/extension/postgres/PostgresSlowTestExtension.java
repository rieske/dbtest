package io.github.rieske.dbtest.extension.postgres;

import org.flywaydb.core.Flyway;

public class PostgresSlowTestExtension extends PostgresTestExtension {
    @Override
    void createFreshMigratedDatabase() {
        executeInPostgresSchema("CREATE DATABASE " + databaseName);
        Flyway.configure()
                .dataSource(dataSourceForDatabase(databaseName))
                .load()
                .migrate();
    }
}
