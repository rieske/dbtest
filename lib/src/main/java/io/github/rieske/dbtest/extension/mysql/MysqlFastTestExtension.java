package io.github.rieske.dbtest.extension.mysql;

public abstract class MysqlFastTestExtension extends MysqlTestExtension {

    private static final String DB_DUMP_FILENAME = "db_dump.sql";

    private static volatile boolean templateDatabaseMigrated = false;

    public MysqlFastTestExtension() {
        if (templateDatabaseMigrated) {
            return;
        }
        synchronized (database) {
            if (templateDatabaseMigrated) {
                return;
            }
            migrateDatabase(database.dataSourceForDatabase(database.getMasterDatabaseName()));
            database.dumpDatabase(DB_DUMP_FILENAME);
            templateDatabaseMigrated = true;
        }
    }

    @Override
    protected void createFreshMigratedDatabase() {
        database.executeInDefaultDatabase("CREATE DATABASE " + databaseName);
        database.restoreDatabase(databaseName, DB_DUMP_FILENAME);
    }
}
