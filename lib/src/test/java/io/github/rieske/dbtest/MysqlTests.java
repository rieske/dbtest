package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.mysql.FlywayMysqlFastTestExtension;
import io.github.rieske.dbtest.extension.mysql.FlywayMysqlSlowTestExtension;
import org.junit.jupiter.api.Nested;

class MysqlTests {
    abstract static class PostgresSlowAndFastTests extends SlowAndFastTests {
        PostgresSlowAndFastTests(String version) {
            super(version, FlywayMysqlSlowTestExtension::new, FlywayMysqlFastTestExtension::new);
        }
    }

    @Nested
    class MysqlDefaultTests extends PostgresSlowAndFastTests {
        MysqlDefaultTests() {
            super("TODO");
        }
    }
}
