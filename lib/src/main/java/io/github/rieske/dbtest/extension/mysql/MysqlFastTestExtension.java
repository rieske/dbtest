package io.github.rieske.dbtest.extension.mysql;

public abstract class MysqlFastTestExtension extends MysqlTestExtension {

    public MysqlFastTestExtension(String databaseVersion) {
        super(databaseVersion);
        database.migrateTemplate(this::migrateDatabase);
    }

    @Override
    protected void createFreshMigratedDatabase() {
        database.executePrivileged("CREATE DATABASE " + databaseName);
        database.restoreDatabase(databaseName);
    }
}
