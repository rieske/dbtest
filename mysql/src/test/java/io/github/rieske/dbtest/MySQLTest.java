package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.FlywayMySQLFastTestExtension;
import io.github.rieske.dbtest.extension.FlywayMySQLSlowTestExtension;

interface MySQLTest {
    default String mysqlVersion() {
        return Environment.getEnvOrDefault("MYSQL_VERSION", "8.0.32");
    }

    default DatabaseTestExtension slowExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayMySQLSlowTestExtension(mysqlVersion(), mode);
    }

    default DatabaseTestExtension fastExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayMySQLFastTestExtension(mysqlVersion(), mode);
    }
}
