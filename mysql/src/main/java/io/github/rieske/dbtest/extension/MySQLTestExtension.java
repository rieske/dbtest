package io.github.rieske.dbtest.extension;

abstract class MySQLTestExtension extends DatabaseTestExtension {

    MySQLTestExtension(String databaseVersion) {
        super(MySQLTestDatabaseManager.getDatabase(databaseVersion));
    }
}
