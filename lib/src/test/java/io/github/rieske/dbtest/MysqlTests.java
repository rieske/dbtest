package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.mysql.FlywayMysqlFastTestExtension;
import io.github.rieske.dbtest.extension.mysql.FlywayMysqlSlowTestExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.RegisterExtension;

class MysqlTests {
    @Nested
    class SlowTests extends DatabaseTest {
        @RegisterExtension
        private final DatabaseTestExtension database = new FlywayMysqlSlowTestExtension();

        @Override
        DatabaseTestExtension database() {
            return database;
        }
    }

    @Nested
    class FastTests extends DatabaseTest {
        @RegisterExtension
        private final DatabaseTestExtension database = new FlywayMysqlFastTestExtension();

        @Override
        DatabaseTestExtension database() {
            return database;
        }
    }
}
