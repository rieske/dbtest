package io.github.rieske.dbtest.extension.mysql;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class FlywayMysqlFastTestExtension extends MysqlFastTestExtension{
    @Override
    protected void migrateDatabase(DataSource dataSource) {
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();
    }
}
