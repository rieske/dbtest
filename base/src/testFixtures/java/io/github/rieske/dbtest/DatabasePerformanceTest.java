package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

abstract class DatabasePerformanceTest {
    private static final int REPETITIONS = 100;

    @RegisterExtension
    private final DatabaseTestExtension database;

    DatabasePerformanceTest(DatabaseTestExtension database) {
        this.database = database;
    }

    @RepeatedTest(REPETITIONS)
    void doNothing() {
    }

    @RepeatedTest(REPETITIONS)
    void interactWithDatabase() {
        UUID id = UUID.randomUUID();
        String foo = UUID.randomUUID().toString();
        database.executeUpdateSql("INSERT INTO some_table(id, foo) VALUES('" + id + "', '" + foo + "')");

        database.executeQuerySql("SELECT COUNT(*) FROM some_table", rs -> {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getLong(1)).isEqualTo(1);
            return null;
        });
        database.executeQuerySql("SELECT * FROM some_table", rs -> {
            assertThat(rs.next()).isTrue();
            assertThat(UUID.fromString(rs.getString(1))).isEqualTo(id);
            assertThat(rs.getString(2)).isEqualTo(foo);
            return null;
        });
    }
}
