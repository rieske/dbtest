package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.FlywayPostgreSQLFastTestExtension;
import io.github.rieske.dbtest.extension.FlywayPostgreSQLSlowTestExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;

class PostgreSQLStateGuaranteeTests implements PostgreSQLTest {
    @Nested
    class PostgreSQLDatabasePerTestMethodTest extends DatabasePerTestMethodTest {
        PostgreSQLDatabasePerTestMethodTest() {
            super(postgresVersion(), FlywayPostgreSQLSlowTestExtension::new, FlywayPostgreSQLFastTestExtension::new);
        }
    }

    @Disabled("not yet implemented")
    @Nested
    class PostgreSQLDatabasePerTestClassTest extends DatabasePerTestClassTest {
        PostgreSQLDatabasePerTestClassTest() {
            super(postgresVersion(), FlywayPostgreSQLSlowTestExtension::new, FlywayPostgreSQLFastTestExtension::new);
        }
    }

    @Disabled("not yet implemented")
    @Nested
    class PostgreSQLDatabasePerExecutionTest extends DatabasePerExecutionTest {
        PostgreSQLDatabasePerExecutionTest() {
            super(postgresVersion(), FlywayPostgreSQLSlowTestExtension::new, FlywayPostgreSQLFastTestExtension::new);
        }
    }
}
