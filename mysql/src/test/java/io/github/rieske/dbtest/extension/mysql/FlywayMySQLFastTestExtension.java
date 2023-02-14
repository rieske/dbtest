package io.github.rieske.dbtest.extension.mysql;

import io.github.rieske.dbtest.FlywayMigrator;

import javax.sql.DataSource;

public class FlywayMySQLFastTestExtension extends MySQLFastTestExtension {

    public FlywayMySQLFastTestExtension(String databaseVersion) {
        super(databaseVersion);
    }

    @Override
    protected void migrateDatabase(DataSource dataSource) {
        FlywayMigrator.migrateDatabase(dataSource);
    }
}
