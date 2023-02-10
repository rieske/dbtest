package io.github.rieske.dbtest.extension.postgres;

import io.github.rieske.dbtest.postgres.PostgresTestExtension;
import org.flywaydb.core.Flyway;

public class PostgresSlowTestExtension extends PostgresTestExtension {
    @Override
    protected void createFreshMigratedDatabase() {
        executeInPostgresSchema("CREATE DATABASE " + databaseName);
        Flyway.configure()
                .dataSource(dataSourceForDatabase(databaseName))
                .load()
                .migrate();
    }
}
