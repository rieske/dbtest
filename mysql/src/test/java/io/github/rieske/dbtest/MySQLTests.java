package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.mysql.FlywayMySQLFastTestExtension;
import io.github.rieske.dbtest.extension.mysql.FlywayMySQLSlowTestExtension;
import org.junit.jupiter.api.Nested;

class MySQLTests {
    abstract static class PostgresSlowAndFastTests extends SlowAndFastTests {
        PostgresSlowAndFastTests(String version) {
            super(version, FlywayMySQLSlowTestExtension::new, FlywayMySQLFastTestExtension::new);
        }
    }

    @Nested
    class Mysql8Tests extends PostgresSlowAndFastTests {
        Mysql8Tests() {
            super("8.0.32");
        }
    }

    @Nested
    class Mysql8031Tests extends PostgresSlowAndFastTests {
        Mysql8031Tests() {
            super("8.0.31");
        }
    }

    @Nested
    class Mysql8030Tests extends PostgresSlowAndFastTests {
        Mysql8030Tests() {
            super("8.0.30");
        }
    }
}