package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.postgres.PostgresFastTestExtension;
import io.github.rieske.dbtest.extension.postgres.PostgresSlowTestExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.RegisterExtension;

public class PostgresTests {
    @Nested
    class SlowTests extends DatabaseTest {
        @RegisterExtension
        private final DatabaseTestExtension database = new PostgresSlowTestExtension();

        @Override
        DatabaseTestExtension database() {
            return database;
        }
    }

    @Nested
    class FastTests extends DatabaseTest {
        @RegisterExtension
        private final DatabaseTestExtension database = new PostgresFastTestExtension();

        @Override
        DatabaseTestExtension database() {
            return database;
        }
    }
}
