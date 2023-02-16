package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.Nested;

class PostgreSQLPerformanceTests implements PostgreSQLTest {
    @Nested
    class PostgreSQLDatabasePerTestMethodPerformanceTest extends PerformanceTests {
        PostgreSQLDatabasePerTestMethodPerformanceTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD));
        }
    }

    @Nested
    class PostgreSQLDatabasePerTestClassPerformanceTest extends PerformanceTests {
        PostgreSQLDatabasePerTestClassPerformanceTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS));
        }
    }

    @Nested
    class PostgreSQLDatabasePerExecutionPerformanceTest extends PerformanceTests {
        PostgreSQLDatabasePerExecutionPerformanceTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION));
        }
    }
}
