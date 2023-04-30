package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.FlywayPostgreSQLFastTestExtension;
import org.junit.jupiter.api.Nested;

public class MultiDatabaseTests {
    private static final String V1 = "15.2";
    private static final String V2 = "14.7";

    @Nested
    class PostgreSQLDatabasePerTestMethodTest extends MultiDatabaseTest {
        PostgreSQLDatabasePerTestMethodTest() {
            super(fastExtensionForVersion(V1, DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD), fastExtensionForVersion(V2, DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD));
        }
    }

    @Nested
    class PostgreSQLDatabasePerTestClassTest extends MultiDatabaseTest {
        PostgreSQLDatabasePerTestClassTest() {
            super(fastExtensionForVersion(V1, DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS), fastExtensionForVersion(V2, DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS));
        }
    }

    @Nested
    class PostgreSQLDatabasePerExecutionTest extends MultiDatabaseTest {
        PostgreSQLDatabasePerExecutionTest() {
            super(fastExtensionForVersion(V1, DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION), fastExtensionForVersion(V2, DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION));
        }
    }

    private static DatabaseTestExtension fastExtensionForVersion(String version, DatabaseTestExtension.Mode mode) {
        return new FlywayPostgreSQLFastTestExtension(version, mode);
    }
}
