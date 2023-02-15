package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.FlywayMySQLFastTestExtension;
import io.github.rieske.dbtest.extension.FlywayMySQLSlowTestExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;

@Disabled("not yet implemented")
class MySQLDatabasePerTestClassTests {
    abstract static class TestTemplate extends DatabasePerTestClassTest {
        TestTemplate(String version) {
            super(version, FlywayMySQLSlowTestExtension::new, FlywayMySQLFastTestExtension::new);
        }
    }

    @Nested
    class Mysql8Tests extends TestTemplate {
        Mysql8Tests() {
            super("8.0.32");
        }
    }

    @Nested
    class Mysql8031Tests extends TestTemplate {
        Mysql8031Tests() {
            super("8.0.31");
        }
    }

    @Nested
    class Mysql8030Tests extends TestTemplate {
        Mysql8030Tests() {
            super("8.0.30");
        }
    }
}
