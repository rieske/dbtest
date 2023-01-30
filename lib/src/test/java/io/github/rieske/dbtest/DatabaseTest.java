package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.RepeatedTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

abstract class DatabaseTest {

    abstract DatabaseTestExtension database();

    @RepeatedTest(100)
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
            assertThat(rs.getObject(1, UUID.class)).isEqualTo(id);
            assertThat(rs.getString(2)).isEqualTo(foo);
            return null;
        });
    }

    @RepeatedTest(100)
    void doNothing() {
    }
}
