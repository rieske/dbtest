package io.github.rieske.dbtest.extension.mysql;

import org.flywaydb.core.Flyway;

public class MysqlFastTestExtension extends MysqlTestExtension {

    private static final String DB_DUMP_FILENAME = "db_dump.sql";

    static {
        Flyway.configure()
                .dataSource(database.dataSourceForDatabase(database.getMasterDatabaseName()))
                .load()
                .migrate();
        database.dumpDatabase(DB_DUMP_FILENAME);
    }

    @Override
    protected void createFreshMigratedDatabase() {
        database.executeInDefaultDatabase("CREATE DATABASE " + databaseName);
        database.restoreDatabase(databaseName, DB_DUMP_FILENAME);
    }
}
