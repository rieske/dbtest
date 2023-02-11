package io.github.rieske.dbtest.extension.postgres;

/**
 * Simulates the traditional approach of creating a fresh database and applying migrations for each test.
 */
abstract class PostgresSlowTestExtension extends PostgresTestExtension {
    @Override
    protected void createFreshMigratedDatabase() {
        database.executeInPostgresSchema("CREATE DATABASE " + databaseName);
        migrateDatabase(database.dataSourceForDatabase(databaseName));
    }
}
