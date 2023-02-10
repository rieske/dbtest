package io.github.rieske.dbtest.extension.mysql;

import io.github.rieske.dbtest.mysql.MysqlTestExtension;
import org.flywaydb.core.Flyway;

public class MysqlSlowTestExtension extends MysqlTestExtension {
    @Override
    protected void createFreshMigratedDatabase() {
        executeInDefaultDatabase("CREATE DATABASE " + databaseName);
        Flyway.configure()
                .dataSource(dataSourceForDatabase(databaseName))
                .load()
                .migrate();
    }
}
