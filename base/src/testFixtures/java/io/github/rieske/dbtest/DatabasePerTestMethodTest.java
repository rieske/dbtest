package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract class DatabasePerTestMethodTest extends DatabaseTest {

    DatabasePerTestMethodTest(DatabaseTestExtension database) {
        super(database);
    }

    @Order(0)
    @Test
    void createState() {
        UUID id = UUID.randomUUID();
        String foo = UUID.randomUUID().toString();
        executeUpdateSql("INSERT INTO some_table(id, foo) VALUES('" + id + "', '" + foo + "')");
    }

    @Order(1)
    @Test
    void ensureNoState() {
        executeQuerySql("SELECT COUNT(*) FROM some_table", rs -> {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getLong(1)).isEqualTo(0);
            return null;
        });
    }
}
