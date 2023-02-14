package io.github.rieske.dbtest.extension.postgresql;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;

abstract class PostgreSQLTestExtension extends DatabaseTestExtension {

    PostgreSQLTestExtension(String databaseVersion) {
        super(PostgreSQLTestDatabaseManager.getDatabase(databaseVersion));
    }
}
