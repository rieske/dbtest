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
     * @param mode            the data retention mode to use for this extension
     */
    public PostgreSQLFastTestExtension(String databaseVersion, Mode mode) {
        super(databaseVersion, mode, true);
    }
}
