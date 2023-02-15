package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.FlywayPostgreSQLFastTestExtension;
import io.github.rieske.dbtest.extension.FlywayPostgreSQLSlowTestExtension;
import org.junit.jupiter.api.Nested;

class PostgreSQLPerformanceTests implements PostgreSQLTest {
    @Nested
    class PostgreSQLDatabasePerTestMethodPerformanceTest extends PerformanceTests {
        PostgreSQLDatabasePerTestMethodPerformanceTest() {
            super(postgresVersion(), FlywayPostgreSQLSlowTestExtension::new, FlywayPostgreSQLFastTestExtension::new);
        }
    }
}
