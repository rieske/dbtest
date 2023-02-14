package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.function.Function;

public abstract class DatabasePerTestMethodTest {
    private final String databaseVersion;
    private final Function<String, DatabaseTestExtension> slowExtensionProvider;
    private final Function<String, DatabaseTestExtension> fastExtensionProvider;

    public DatabasePerTestMethodTest(
            String databaseVersion,
            Function<String, DatabaseTestExtension> slowExtensionProvider,
            Function<String, DatabaseTestExtension> fastExtensionProvider
    ) {
        this.databaseVersion = databaseVersion;
        this.slowExtensionProvider = slowExtensionProvider;
        this.fastExtensionProvider = fastExtensionProvider;
    }

    @Nested
    class SlowTest extends TestTemplate {
        SlowTest() {
            super(slowExtensionProvider.apply(databaseVersion));
        }
    }

    @Nested
    class FastTest extends TestTemplate {
        FastTest() {
            super(fastExtensionProvider.apply(databaseVersion));
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
