package io.github.rieske.dbtest;

import org.junit.jupiter.api.RepeatedTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class DatabaseTest {
    private static final int REPETITIONS = 100;

    abstract DatabaseTestExtension database();

    @RepeatedTest(REPETITIONS)
    void interactWithDatabase() {
        var id = UUID.randomUUID();
        var foo = UUID.randomUUID().toString();
        database().executeUpdateSql("INSERT INTO some_table(id, foo) VALUES('%s', '%s')".formatted(id, foo));

        database().executeQuerySql("SELECT COUNT(*) FROM some_table", rs -> {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getLong(1)).isEqualTo(1);
            return null;
        });
        database().executeQuerySql("SELECT * FROM some_table", rs -> {
            assertThat(rs.next()).isTrue();
            assertThat(UUID.fromString(rs.getString(1))).isEqualTo(id);
            assertThat(rs.getString(2)).isEqualTo(foo);
            return null;
        });
    }

    @RepeatedTest(REPETITIONS)
    void doNothing() {
    }
}
