package io.github.rieske.dbtest.extension.postgres;

public abstract class PostgresFastTestExtension extends PostgresTestExtension {

    public PostgresFastTestExtension(String databaseVersion) {
        super(databaseVersion);
        database.migrateTemplateDatabase(this::migrateDatabase);
    }

    @Override
    protected void createFreshMigratedDatabase() {
        database.cloneTemplateDatabaseTo(databaseName);
    }
}
