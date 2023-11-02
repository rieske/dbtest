package io.github.rieske.dbtest.lifecycle;

import io.github.rieske.dbtest.PostgreSQLTest;
import io.github.rieske.dbtest.TestRepository;
import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.FlywayPostgreSQLFastTestExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

class FieldExtensionLifecycleTests {

    @Nested
    class PerMethodTests {
        @RegisterExtension
        final DatabaseTestExtension database =
                new FlywayPostgreSQLFastTestExtension(PostgreSQLTest.postgresVersion(), DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD);

        private final TestRepository repository = new TestRepository(database.getDataSource());

        @Test
        void interactWithDatabase() {
            assertThat(repository.getRecordCount()).isZero();
            repository.insertRandomRecord();
            assertThat(repository.getRecordCount()).isOne();
        }
    }

    @Nested
    class PerClassTests {
        @RegisterExtension
        final DatabaseTestExtension database =
                new FlywayPostgreSQLFastTestExtension(PostgreSQLTest.postgresVersion(), DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS);

        private final EarlyDatabaseExtensionUser exceptionSupplier = new EarlyDatabaseExtensionUser(database);

        @Test
        void throwsWhenPerClassExtensionIsRegisteredAsInstanceFieldAndAccessedDuringTestInstantiation() {
            assertThat(exceptionSupplier.expectedException).hasMessage("Per-class database extension must be registered as a static test class field in order to use the datasource during test instance construction.");
        }

        class EarlyDatabaseExtensionUser {
            private final IllegalStateException expectedException;

            EarlyDatabaseExtensionUser(DatabaseTestExtension extension) {
                try {
                    extension.getDataSource();
                    throw new RuntimeException("Expected an IllegalStateException to be thrown");
                } catch (IllegalStateException e) {
                    this.expectedException = e;
                }
            }
        }
    }

    @Nested
    class PerExecutionTests {
        @RegisterExtension
        final DatabaseTestExtension database =
                new FlywayPostgreSQLFastTestExtension(PostgreSQLTest.postgresVersion(), DatabaseTestExtension.Mode.DATABASE_PER_EXECUTION);

        private final TestRepository repository = new TestRepository(database.getDataSource());

        @Test
        void interactWithDatabase() {
            int recordCount = repository.getRecordCount();
            repository.insertRandomRecord();
            assertThat(repository.getRecordCount()).isGreaterThan(recordCount);
        }
    }
}
