package io.github.rieske.dbtest.lifecycle;

import io.github.rieske.dbtest.PostgreSQLTest;
import io.github.rieske.dbtest.TestRepository;
import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.FlywayPostgreSQLFastTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

class PerClassStaticExtensionTest {
    @RegisterExtension
    private static final DatabaseTestExtension database =
            new FlywayPostgreSQLFastTestExtension(PostgreSQLTest.postgresVersion(), DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS);

    private final TestRepository repository = new TestRepository(database.getDataSource());

    @Test
    void interactWithDatabase() {
        assertThat(repository.getRecordCount()).isZero();
        repository.insertRandomRecord();
        assertThat(repository.getRecordCount()).isOne();
    }
}
