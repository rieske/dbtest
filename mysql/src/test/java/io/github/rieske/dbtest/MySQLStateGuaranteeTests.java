package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.FlywayMySQLFastTestExtension;
import io.github.rieske.dbtest.extension.FlywayMySQLSlowTestExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;

class MySQLStateGuaranteeTests implements MySQLTest {
    @Nested
    class MySQLDatabasePerTestMethodTest extends DatabasePerTestMethodTest {
        MySQLDatabasePerTestMethodTest() {
            super(mysqlVersion(), FlywayMySQLSlowTestExtension::new, FlywayMySQLFastTestExtension::new);
        }
    }

    @Disabled("not yet implemented")
    @Nested
    class MySQLDatabasePerTestClassTest extends DatabasePerTestClassTest {
        MySQLDatabasePerTestClassTest() {
            super(mysqlVersion(), FlywayMySQLSlowTestExtension::new, FlywayMySQLFastTestExtension::new);
        }
    }

    @Disabled("not yet implemented")
    @Nested
    class MySQLDatabasePerExecutionTest extends DatabasePerExecutionTest {
        MySQLDatabasePerExecutionTest() {
            super(mysqlVersion(), FlywayMySQLSlowTestExtension::new, FlywayMySQLFastTestExtension::new);
        }
    }
}
