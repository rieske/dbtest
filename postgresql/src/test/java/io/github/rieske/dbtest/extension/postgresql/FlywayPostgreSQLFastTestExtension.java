package io.github.rieske.dbtest.extension.postgresql;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class FlywayPostgreSQLFastTestExtension extends PostgreSQLFastTestExtension {
    public FlywayPostgreSQLFastTestExtension(String databaseVersion) {
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
