package io.github.rieske.dbtest.extension;

/**
 * Simulates the traditional approach of creating a fresh database and applying migrations for each test.
 */
abstract class PostgreSQLSlowTestExtension extends PostgreSQLTestExtension {
    PostgreSQLSlowTestExtension(String databaseVersion) {
        super(databaseVersion);
    }

    @Override
    void createFreshMigratedDatabase() {
        createEmptyTestDatabase();
        migrateDatabase(getDataSource());
    }
}
