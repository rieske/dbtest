package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.mysql.MysqlFastTestExtension;
import io.github.rieske.dbtest.extension.mysql.MysqlSlowTestExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.RegisterExtension;

class MysqlTests {
    @Nested
    class SlowTests extends DatabaseTest {
        @RegisterExtension
        private final DatabaseTestExtension database = new MysqlSlowTestExtension();

        @Override
        DatabaseTestExtension database() {
            return database;
        }
    }

    @Nested
    class FastTests extends DatabaseTest {
        @RegisterExtension
        private final DatabaseTestExtension database = new MysqlFastTestExtension();

        @Override
        DatabaseTestExtension database() {
            return database;
        }
    }
}
