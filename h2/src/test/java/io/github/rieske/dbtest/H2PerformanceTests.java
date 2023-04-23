package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.Nested;

class H2PerformanceTests implements H2Test {
    @Nested
    class H2DatabasePerTestMethodPerformanceTest extends PerformanceTests {
        H2DatabasePerTestMethodPerformanceTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD));
        }
    }

    @Nested
    class H2DatabasePerTestClassPerformanceTest extends PerformanceTests {
        H2DatabasePerTestClassPerformanceTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS));
        }
    }

    @Nested
    class H2DatabasePerExecutionPerformanceTest extends PerformanceTests {
        H2DatabasePerExecutionPerformanceTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION));
        }
    }
}
