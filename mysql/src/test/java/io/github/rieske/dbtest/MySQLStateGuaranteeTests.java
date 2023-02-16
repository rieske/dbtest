package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.Nested;

class MySQLStateGuaranteeTests implements MySQLTest {
    @Nested
    class MySQLDatabasePerTestMethodTest extends DatabasePerTestMethodTest {
        MySQLDatabasePerTestMethodTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD));
        }
    }

    @Nested
    class MySQLDatabasePerTestClassTest extends DatabasePerTestClassTest {
        MySQLDatabasePerTestClassTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS));
        }
    }

    @Nested
    class MySQLDatabasePerExecutionTest extends DatabasePerExecutionTest {
        MySQLDatabasePerExecutionTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION));
        }
    }
}
