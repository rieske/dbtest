package io.github.rieske.dbtest.extension.postgres;

/**
 * Simulates the traditional approach of creating a fresh database and applying migrations for each test.
 */
abstract class PostgresSlowTestExtension extends PostgresTestExtension {
    PostgresSlowTestExtension(String databaseVersion) {
        super(databaseVersion);
    }

    @Override
    protected void createFreshMigratedDatabase() {
        database.executePrivileged("CREATE DATABASE " + databaseName);
        migrateDatabase(database.dataSourceForDatabase(databaseName));
    }
}
