package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.RepeatedTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

abstract class DatabasePerformanceTest extends DatabaseTest {
    private static final int REPETITIONS = 100;

    DatabasePerformanceTest(DatabaseTestExtension database) {
        super(database);
    }

    @RepeatedTest(REPETITIONS)
    void doNothing() {
    }

    @RepeatedTest(REPETITIONS)
    void interactWithDatabase() {
        UUID id = UUID.randomUUID();
        String foo = UUID.randomUUID().toString();
        executeUpdateSql("INSERT INTO some_table(id, foo) VALUES('" + id + "', '" + foo + "')");

        assertRecordCount(1);

        executeQuerySql("SELECT * FROM some_table", rs -> {
            assertThat(rs.next()).isTrue();
            assertThat(UUID.fromString(rs.getString(1))).isEqualTo(id);
            assertThat(rs.getString(2)).isEqualTo(foo);
            return null;
        });
    }
}
