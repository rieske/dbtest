package io.github.rieske.dbtest.extension.mysql;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class FlywayMysqlSlowTestExtension extends MysqlSlowTestExtension{

    public FlywayMysqlSlowTestExtension(String databaseVersion) {
        // TODO:
    }

    @Override
    protected void migrateDatabase(DataSource dataSource) {
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();
    }
}
