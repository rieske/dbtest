package io.github.rieske.dbtest.extension.mysql;

import org.flywaydb.core.Flyway;

public class MysqlSlowTestExtension extends MysqlTestExtension {
    @Override
    protected void createFreshMigratedDatabase() {
        database.executeInDefaultDatabase("CREATE DATABASE " + databaseName);
        Flyway.configure()
                .dataSource(database.dataSourceForDatabase(databaseName))
                .load()
                .migrate();
    }
}
