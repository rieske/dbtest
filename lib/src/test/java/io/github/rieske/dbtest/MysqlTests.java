package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.mysql.FlywayMysqlFastTestExtension;
import io.github.rieske.dbtest.extension.mysql.FlywayMysqlSlowTestExtension;
import org.junit.jupiter.api.Nested;

class MysqlTests {
    @Nested
    class SlowTests extends DatabaseTest {
        SlowTests() {
            super(new FlywayMysqlSlowTestExtension());
        }
    }

    @Nested
    class FastTests extends DatabaseTest {
        FastTests() {
            super(new FlywayMysqlFastTestExtension());
        }
    }
}
