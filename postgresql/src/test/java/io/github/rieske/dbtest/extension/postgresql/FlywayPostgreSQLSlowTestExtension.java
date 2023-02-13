package io.github.rieske.dbtest.extension.postgresql;

import io.github.rieske.dbtest.FlywayMigrator;

import javax.sql.DataSource;

public class FlywayPostgreSQLSlowTestExtension extends PostgreSQLSlowTestExtension {
    public FlywayPostgreSQLSlowTestExtension(String databaseVersion) {
        super(databaseVersion);
    }

    @Override
    protected void migrateDatabase(DataSource dataSource) {
        FlywayMigrator.migrateDatabase(dataSource);
    }
}
