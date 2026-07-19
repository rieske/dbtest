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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

// Each extension instance has its own DATABASE_PER_EXECUTION database. Track expected
// record counts per extension so parallel top-level suites (and Slow vs Fast) do not
// race a shared static counter. Keep each suite single-threaded for ordered nested tests.
@Execution(ExecutionMode.SAME_THREAD)
public abstract class DatabasePerExecutionTest {
    private static final ConcurrentHashMap<DatabaseTestExtension, AtomicInteger> RECORD_COUNTS =
            new ConcurrentHashMap<>();

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
        TestTemplate(DatabaseTestExtension database) {
            super(database);
        }

        private AtomicInteger currentRecordCount() {
            return RECORD_COUNTS.computeIfAbsent(database, ignored -> new AtomicInteger(0));
        }

        @Order(0)
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        @Nested
        class FirstTestClass {

            @Order(1)
            @Test
            void createState() {
                AtomicInteger currentRecordCount = currentRecordCount();
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
                assertThat(recordCount).isGreaterThanOrEqualTo(currentRecordCount().get());
            }
        }

        @Order(3)
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        @Nested
        class SecondTestClass {
            @Order(4)
            @Test
            void createState() {
                AtomicInteger currentRecordCount = currentRecordCount();
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
                assertThat(recordCount).isGreaterThanOrEqualTo(currentRecordCount().get());
            }
        }
    }
}
