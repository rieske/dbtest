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
        insertRandomRecord();
    }

    @Order(1)
    @Test
    void ensureNoState() {
        assertRecordCount(0);
    }
}
