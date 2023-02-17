package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.Nested;

class PostgreSQLStateGuaranteeTests implements PostgreSQLTest {
    @Nested
    class PostgreSQLDatabasePerTestMethodTest extends DatabasePerTestMethodTest {
        PostgreSQLDatabasePerTestMethodTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD));
        }
    }

    @Nested
    class PostgreSQLDatabasePerTestClassTest extends DatabasePerTestClassTest {
        PostgreSQLDatabasePerTestClassTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS));
        }
    }

    @Nested
    class PostgreSQLDatabasePerExecutionTest extends DatabasePerExecutionTest {
        PostgreSQLDatabasePerExecutionTest() {
            super(slowExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION), fastExtension(DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION));
        }
    }
}
