package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.Nested;

class MySQLPerformanceTests implements MySQLTest {
    @Nested
    class MySQLDatabasePerTestMethodPerformanceTest extends PerformanceTests {
        MySQLDatabasePerTestMethodPerformanceTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD));
        }
    }

    @Nested
    class MySQLDatabasePerTestClassPerformanceTest extends PerformanceTests {
        MySQLDatabasePerTestClassPerformanceTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS));
        }
    }

    @Nested
    class MySQLDatabasePerExecutionPerformanceTest extends PerformanceTests {
        MySQLDatabasePerExecutionPerformanceTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION));
        }
    }
}
