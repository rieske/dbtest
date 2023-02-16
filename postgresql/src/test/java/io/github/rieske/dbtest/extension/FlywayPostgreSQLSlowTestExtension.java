package io.github.rieske.dbtest.extension;

import io.github.rieske.dbtest.FlywayMigrator;

import javax.sql.DataSource;

public class FlywayPostgreSQLSlowTestExtension extends PostgreSQLSlowTestExtension {
    public FlywayPostgreSQLSlowTestExtension(String databaseVersion, Mode mode) {
        super(databaseVersion, mode);
    }

    @Override
    protected void migrateDatabase(DataSource dataSource) {
        FlywayMigrator.migrateDatabase(dataSource);
    }
}
