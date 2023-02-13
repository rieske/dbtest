package io.github.rieske.dbtest.extension.postgresql;

import io.github.rieske.dbtest.FlywayMigrator;

import javax.sql.DataSource;

public class FlywayPostgreSQLFastTestExtension extends PostgreSQLFastTestExtension {
    public FlywayPostgreSQLFastTestExtension(String databaseVersion) {
        super(databaseVersion);
    }

    @Override
    protected void migrateDatabase(DataSource dataSource) {
        FlywayMigrator.migrateDatabase(dataSource);
    }
}
