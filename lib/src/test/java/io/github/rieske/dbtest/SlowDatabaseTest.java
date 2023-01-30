package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.postgres.PostgresSlowTestExtension;
import org.junit.jupiter.api.extension.RegisterExtension;

class SlowDatabaseTest extends DatabaseTest {
    @RegisterExtension
    private final DatabaseTestExtension database = new PostgresSlowTestExtension();

    @Override
    DatabaseTestExtension database() {
        return database;
    }
}
