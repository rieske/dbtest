package io.github.rieske.dbtest.extension.mysql;

/**
 * Simulates the traditional approach of creating a fresh database and applying migrations for each test.
 */
abstract class MysqlSlowTestExtension extends MysqlTestExtension {
    MysqlSlowTestExtension(String databaseVersion) {
        super(databaseVersion);
    }

    @Override
    protected void createFreshMigratedDatabase() {
        database.executePrivileged("CREATE DATABASE " + databaseName);
        migrateDatabase(database.dataSourceForDatabase(databaseName));
    }
}
