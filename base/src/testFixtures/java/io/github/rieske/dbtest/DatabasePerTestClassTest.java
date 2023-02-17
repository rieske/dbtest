package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

public abstract class DatabasePerTestClassTest {
    private final DatabaseTestExtension slowExtension;
    private final DatabaseTestExtension fastExtension;

    public DatabasePerTestClassTest(DatabaseTestExtension slowExtension, DatabaseTestExtension fastExtension) {
        this.slowExtension = slowExtension;
        this.fastExtension = fastExtension;
    }

    @Nested
    class SlowTest extends DatabaseTest {
        SlowTest() {
            super(slowExtension);
        }

        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        @Nested
        class FirstTestClass {
            @Order(0)
            @Test
            void createState() {
                assertRecordCount(0);
                insertRandomRecord();
                assertRecordCount(1);
            }

            @Order(1)
            @Test
            void ensureState() {
                assertRecordCount(1);
            }
        }

        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        @Nested
        class SecondTestClass {
            @Order(0)
            @Test
            void createState() {
                assertRecordCount(0);
                insertRandomRecord();
                assertRecordCount(1);
            }

            @Order(1)
            @Test
            void ensureState() {
                assertRecordCount(1);
            }
        }
    }

    @Nested
    class FastTest extends DatabaseTest {
        FastTest() {
            super(fastExtension);
        }

        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        @Nested
        class FirstTestClass {
            @Order(0)
            @Test
            void createState() {
                assertRecordCount(0);
                insertRandomRecord();
                assertRecordCount(1);
            }

            @Order(1)
            @Test
            void ensureState() {
                assertRecordCount(1);
            }
        }

        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        @Nested
        class SecondTestClass {
            @Order(0)
            @Test
            void createState() {
                assertRecordCount(0);
                insertRandomRecord();
                assertRecordCount(1);
            }

            @Order(1)
            @Test
            void ensureState() {
                assertRecordCount(1);
            }
        }
    }
}
