package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.Nested;

class H2StateGuaranteeTests implements H2Test {
    @Nested
    class H2DatabasePerTestMethodTest extends DatabasePerTestMethodTest {
        H2DatabasePerTestMethodTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD));
        }
    }

    @Nested
    class H2DatabasePerTestClassTest extends DatabasePerTestClassTest {
        H2DatabasePerTestClassTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS));
        }
    }

    @Nested
    class H2DatabasePerExecutionTest extends DatabasePerExecutionTest {
        H2DatabasePerExecutionTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION));
        }
    }
}
