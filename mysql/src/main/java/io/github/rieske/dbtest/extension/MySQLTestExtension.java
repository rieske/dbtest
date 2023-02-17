package io.github.rieske.dbtest.extension;

abstract class MySQLTestExtension extends DatabaseTestExtension {

    MySQLTestExtension(String databaseVersion, Mode mode, boolean migrateOnce) {
        super(MySQLTestDatabaseManager.getDatabase(databaseVersion), mode, migrateOnce);
    }
}
