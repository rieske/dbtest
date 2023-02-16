package io.github.rieske.dbtest.extension;

/**
 * JUnit5 extension that enables you to write tests against a containerized MySQL database.
 */
public abstract class MySQLFastTestExtension extends MySQLTestExtension {

    /**
     * Create a MySQL database extension using the official <a href="https://hub.docker.com/_/mysql">MySQL Docker image</a>.
     *
     * @param databaseVersion the database version to use.
     *                        Must be a valid docker image tag from the official <a href="https://hub.docker.com/_/mysql/tags">MySQL Docker image</a>.
     * @param mode the data retention mode to use for this extension
     */
    public MySQLFastTestExtension(String databaseVersion, Mode mode) {
        super(databaseVersion, mode);
        migrateTemplateDatabase();
    }

    @Override
    void createFreshMigratedDatabase() {
        cloneTemplateDatabaseToTestDatabase();
    }
}
