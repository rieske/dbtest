package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.FlywayPostgreSQLFastTestExtension;
import io.github.rieske.dbtest.extension.FlywayPostgreSQLSlowTestExtension;

public interface PostgreSQLTest {
    static String postgresVersion() {
        return Environment.getEnvOrDefault("POSTGRES_VERSION", "16.0");
    }

    default DatabaseTestExtension slowExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayPostgreSQLSlowTestExtension(postgresVersion(), mode);
    }

    default DatabaseTestExtension fastExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayPostgreSQLFastTestExtension(postgresVersion(), mode);
    }
}
