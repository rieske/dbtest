package io.github.rieske.dbtest.extension.postgres;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class FlywayPostgresSlowTestExtension extends PostgresSlowTestExtension {
    @Override
    protected void migrateDatabase(DataSource dataSource) {
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();
    }
}
