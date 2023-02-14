package io.github.rieske.dbtest.extension.mysql;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;

abstract class MySQLTestExtension extends DatabaseTestExtension {

    MySQLTestExtension(String databaseVersion) {
        super(MySQLTestDatabaseManager.getDatabase(databaseVersion));
    }
}
