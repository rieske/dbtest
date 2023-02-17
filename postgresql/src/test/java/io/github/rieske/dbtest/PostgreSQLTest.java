package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.FlywayPostgreSQLFastTestExtension;
import io.github.rieske.dbtest.extension.FlywayPostgreSQLSlowTestExtension;

interface PostgreSQLTest {
    default String postgresVersion() {
        return Environment.getEnvOrDefault("POSTGRES_VERSION", "15.2-alpine");
    }

    default DatabaseTestExtension slowExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayPostgreSQLSlowTestExtension(postgresVersion(), mode);
    }

    default DatabaseTestExtension fastExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayPostgreSQLFastTestExtension(postgresVersion(), mode);
    }
}
