package io.github.rieske.dbtest.extension;

/**
 * JUnit5 extension that enables you to write tests against a containerized PostgreSQL database.
 */
public abstract class PostgreSQLFastTestExtension extends PostgreSQLTestExtension {

    /**
     * Create a PostgreSQL database extension using the official <a href="https://hub.docker.com/_/postgres">PostgreSQL Docker image</a>.
     *
     * @param databaseVersion the database version to use.
     *                        Must be a valid docker image tag from the official <a href="https://hub.docker.com/_/postgres/tags">PostgreSQL Docker image</a>.
     */
    public PostgreSQLFastTestExtension(String databaseVersion) {
        super(databaseVersion);
        database.migrateTemplateDatabase(this::migrateDatabase);
    }

    @Override
    void createFreshMigratedDatabase() {
        database.cloneTemplateDatabaseTo(databaseName);
    }
}
