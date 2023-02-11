package io.github.rieske.dbtest.extension.postgres;

public abstract class PostgresFastTestExtension extends PostgresTestExtension {

    private static volatile boolean templateDatabaseMigrated = false;

    public PostgresFastTestExtension() {
        if (templateDatabaseMigrated) {
            return;
        }
        synchronized (database) {
            if (templateDatabaseMigrated) {
                return;
            }
            migrateDatabase(database.dataSourceForDatabase(database.getMasterDatabaseName()));
            templateDatabaseMigrated = true;
        }
    }

    @Override
    protected void createFreshMigratedDatabase() {
        database.executeInPostgresSchema("CREATE DATABASE " + databaseName + " TEMPLATE " + database.getMasterDatabaseName());
    }
}
