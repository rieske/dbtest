package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class DatabasePerExecutionTest {
    private final DatabaseTestExtension slowExtension;
    private final DatabaseTestExtension fastExtension;

    public DatabasePerExecutionTest(DatabaseTestExtension slowExtension, DatabaseTestExtension fastExtension) {
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

    @Execution(ExecutionMode.SAME_THREAD)
    @TestClassOrder(ClassOrderer.OrderAnnotation.class)
    private abstract static class TestTemplate extends DatabaseTest {
        private static final AtomicInteger currentRecordCount = new AtomicInteger(0);

        TestTemplate(DatabaseTestExtension database) {
            super(database);
        }

        @Order(0)
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        @Nested
        class FirstTestClass {

            @Order(1)
            @Test
            void createState() {
                currentRecordCount.set(getRecordCount());
                insertRandomRecord();
                int expectedRecordCount = currentRecordCount.incrementAndGet();
                int recordCount = getRecordCount();
                assertThat(recordCount).isGreaterThanOrEqualTo(expectedRecordCount);
            }

            @Order(2)
            @Test
            void ensureState() {
                int recordCount = getRecordCount();
                assertThat(recordCount).isGreaterThan(0);
                assertThat(recordCount).isGreaterThanOrEqualTo(currentRecordCount.get());
            }
        }

        @Order(3)
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        @Nested
        class SecondTestClass {
            @Order(4)
            @Test
            void createState() {
                int recordCount = getRecordCount();
                assertThat(recordCount).isGreaterThan(0);
                assertThat(recordCount).isGreaterThanOrEqualTo(currentRecordCount.get());
                insertRandomRecord();
                int expectedRecordCount = currentRecordCount.incrementAndGet();
                recordCount = getRecordCount();
                assertThat(recordCount).isGreaterThanOrEqualTo(expectedRecordCount);
            }

            @Order(5)
            @Test
            void ensureState() {
                int recordCount = getRecordCount();
                assertThat(recordCount).isGreaterThan(1);
                assertThat(recordCount).isGreaterThanOrEqualTo(currentRecordCount.get());
            }
        }
    }
}
