package io.github.rieske.dbtest.extension;

abstract class PostgreSQLTestExtension extends DatabaseTestExtension {
    PostgreSQLTestExtension(String databaseVersion, Mode mode) {
        super(PostgreSQLTestDatabaseManager.getDatabase(databaseVersion), mode);
    }
}
