package io.github.rieske.dbtest;

interface PostgreSQLTest {
    default String postgresVersion() {
        return Environment.getEnvOrDefault("POSTGRES_VERSION", "15.2-alpine");
    }
}
