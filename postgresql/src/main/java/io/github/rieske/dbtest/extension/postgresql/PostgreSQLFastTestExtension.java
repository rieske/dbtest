package io.github.rieske.dbtest.extension.postgresql;

public abstract class PostgreSQLFastTestExtension extends PostgreSQLTestExtension {

    public PostgreSQLFastTestExtension(String databaseVersion) {
        super(databaseVersion);
        database.migrateTemplateDatabase(this::migrateDatabase);
    }

    @Override
    protected void createFreshMigratedDatabase() {
        database.cloneTemplateDatabaseTo(databaseName);
    }
}
