package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.PostgresTestExtension;
import org.junit.jupiter.api.extension.RegisterExtension;

class FastDatabaseTest extends DatabaseTest {
    @RegisterExtension
    private final DatabaseTestExtension database = new PostgresTestExtension();

    @Override
    DatabaseTestExtension database() {
        return database;
    }
}
