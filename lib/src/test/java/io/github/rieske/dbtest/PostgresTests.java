package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.postgres.FlywayPostgresFastTestExtension;
import io.github.rieske.dbtest.extension.postgres.FlywayPostgresSlowTestExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.RegisterExtension;

public class PostgresTests {
    @Nested
    class SlowTests extends DatabaseTest {
        @RegisterExtension
        private final DatabaseTestExtension database = new FlywayPostgresSlowTestExtension();

        @Override
        DatabaseTestExtension database() {
            return database;
        }
    }

    @Nested
    class FastTests extends DatabaseTest {
        @RegisterExtension
        private final DatabaseTestExtension database = new FlywayPostgresFastTestExtension();

        @Override
        DatabaseTestExtension database() {
            return database;
        }
    }
}
