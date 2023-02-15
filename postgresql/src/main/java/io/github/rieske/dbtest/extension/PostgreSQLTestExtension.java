package io.github.rieske.dbtest.extension;

abstract class PostgreSQLTestExtension extends DatabaseTestExtension {

    PostgreSQLTestExtension(String databaseVersion) {
        super(PostgreSQLTestDatabaseManager.getDatabase(databaseVersion));
    }
}
