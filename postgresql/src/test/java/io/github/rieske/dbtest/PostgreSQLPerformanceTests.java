package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.FlywayPostgreSQLFastTestExtension;
import io.github.rieske.dbtest.extension.FlywayPostgreSQLSlowTestExtension;
import org.junit.jupiter.api.Nested;

class PostgreSQLPerformanceTests {
    abstract static class TestTemplate extends PerformanceTests {
        TestTemplate(String version) {
            super(version, FlywayPostgreSQLSlowTestExtension::new, FlywayPostgreSQLFastTestExtension::new);
        }
    }

    @Nested
    class Postgres15Tests extends TestTemplate {
        Postgres15Tests() {
            super("15.2-alpine");
        }
    }

    @Nested
    class Postgres14Tests extends TestTemplate {
        Postgres14Tests() {
            super("14.7-alpine");
        }
    }

    @Nested
    class Postgres13Tests extends TestTemplate {
        Postgres13Tests() {
            super("13.10-alpine");
        }
    }
}
