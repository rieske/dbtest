package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

public abstract class DatabasePerTestMethodTest {
    private final DatabaseTestExtension slowExtension;
    private final DatabaseTestExtension fastExtension;

    public DatabasePerTestMethodTest(DatabaseTestExtension slowExtension, DatabaseTestExtension fastExtension) {
        this.slowExtension = slowExtension;
        this.fastExtension = fastExtension;
    }

    @Nested
    class SlowTest extends TestTemplate {
        SlowTest() {
            super(slowExtension);
        }
    }

    @Nested
    class FastTest extends TestTemplate {
        FastTest() {
            super(fastExtension);
        }
    }

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    abstract static class TestTemplate extends DatabaseTest {

        TestTemplate(DatabaseTestExtension database) {
            super(database);
        }

        @Order(0)
        @Test
        void createState() {
            assertRecordCount(0);
            insertRandomRecord();
            assertRecordCount(1);
        }

        @Order(1)
        @Test
        void ensureNoState() {
            assertRecordCount(0);
        }
    }
}
