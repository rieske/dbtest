package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.FlywayMySQLFastTestExtension;
import io.github.rieske.dbtest.extension.FlywayMySQLSlowTestExtension;
import org.junit.jupiter.api.Nested;

class MySQLPerformanceTests implements MySQLTest {
    @Nested
    class MySQLDatabasePerTestMethodPerformanceTest extends PerformanceTests {
        MySQLDatabasePerTestMethodPerformanceTest() {
            super(mysqlVersion(), FlywayMySQLSlowTestExtension::new, FlywayMySQLFastTestExtension::new);
        }
    }
}
