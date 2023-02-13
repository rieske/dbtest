package io.github.rieske.dbtest.extension.mysql;

import io.github.rieske.dbtest.FlywayMigrator;

import javax.sql.DataSource;

public class FlywayMysqlSlowTestExtension extends MysqlSlowTestExtension {

    public FlywayMysqlSlowTestExtension(String databaseVersion) {
        super(databaseVersion);
    }

    @Override
    protected void migrateDatabase(DataSource dataSource) {
        FlywayMigrator.migrateDatabase(dataSource);
    }
}
