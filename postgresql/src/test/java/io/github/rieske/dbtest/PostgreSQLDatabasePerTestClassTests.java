package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.postgresql.FlywayPostgreSQLFastTestExtension;
import io.github.rieske.dbtest.extension.postgresql.FlywayPostgreSQLSlowTestExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;

@Disabled("not yet implemented")
class PostgreSQLDatabasePerTestClassTests {
    abstract static class TestTemplate extends DatabasePerTestClassTest {
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