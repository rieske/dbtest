package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.postgresql.FlywayPostgreSQLFastTestExtension;
import io.github.rieske.dbtest.extension.postgresql.FlywayPostgreSQLSlowTestExtension;
import org.junit.jupiter.api.Nested;

class PostgreSQLTests {
    abstract static class PostgresSlowAndFastTests extends SlowAndFastTests {
        PostgresSlowAndFastTests(String version) {
            super(version, FlywayPostgreSQLSlowTestExtension::new, FlywayPostgreSQLFastTestExtension::new);
        }
    }

    @Nested
    class Postgres15Tests extends PostgresSlowAndFastTests {
        Postgres15Tests() {
            super("15.2-alpine");
        }
    }

    @Nested
    class Postgres14Tests extends PostgresSlowAndFastTests {
        Postgres14Tests() {
            super("14.7-alpine");
        }
    }

    @Nested
    class Postgres13Tests extends PostgresSlowAndFastTests {
        Postgres13Tests() {
            super("13.10-alpine");
        }
    }
}
