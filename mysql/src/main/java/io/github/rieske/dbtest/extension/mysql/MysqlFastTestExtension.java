package io.github.rieske.dbtest.extension.mysql;

public abstract class MysqlFastTestExtension extends MysqlTestExtension {

    public MysqlFastTestExtension(String databaseVersion) {
        super(databaseVersion);
        database.migrateTemplateDatabase(this::migrateDatabase);
    }

    @Override
    protected void createFreshMigratedDatabase() {
        database.cloneTemplateDatabaseTo(databaseName);
    }
}
