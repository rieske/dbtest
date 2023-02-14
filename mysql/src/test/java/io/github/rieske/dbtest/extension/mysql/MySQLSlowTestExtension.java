package io.github.rieske.dbtest.extension.mysql;

/**
 * Simulates the traditional approach of creating a fresh database and applying migrations for each test.
 */
abstract class MySQLSlowTestExtension extends MySQLTestExtension {
    MySQLSlowTestExtension(String databaseVersion) {
        super(databaseVersion);
    }

    @Override
    protected void createFreshMigratedDatabase() {
        database.createDatabase(databaseName);
        migrateDatabase(getDataSource());
    }
}