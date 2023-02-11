package io.github.rieske.dbtest.extension.postgres;

public abstract class PostgresFastTestExtension extends PostgresTestExtension {

    public PostgresFastTestExtension(String databaseVersion) {
        super(databaseVersion);
        database.migrateTemplate(this::migrateDatabase);
    }

    @Override
    protected void createFreshMigratedDatabase() {
        database.executePrivileged("CREATE DATABASE " + databaseName + " TEMPLATE " + database.getTemplateDatabaseName());
    }
}
