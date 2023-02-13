package io.github.rieske.dbtest.extension.postgresql;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class FlywayPostgreSQLSlowTestExtension extends PostgreSQLSlowTestExtension {
    public FlywayPostgreSQLSlowTestExtension(String databaseVersion) {
        super(databaseVersion);
    }

    @Override
    protected void migrateDatabase(DataSource dataSource) {
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();
    }
}
