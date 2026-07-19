package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.FlywayH2PostgreSQLFastTestExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Stresses a single shared extension under concurrent getDataSource() usage.
 * Nested suites below also share one extension instance across concurrent nested classes.
 */
class ParallelExtensionRaceTest {

    @Nested
    @Execution(ExecutionMode.CONCURRENT)
    class SharedExtensionNestedClasses {
        // Shared by both nested classes — mirrors real SlowTest/FastTest fixture shape
        @RegisterExtension
        static final DatabaseTestExtension database =
                new FlywayH2PostgreSQLFastTestExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_CLASS);

        @Nested
        @Execution(ExecutionMode.CONCURRENT)
        class SuiteA {
            @Test
            void writeAndRead() throws Exception {
                assertIsolatedRoundTrip("suite-a");
            }

            @Test
            void writeAndReadAgain() throws Exception {
                assertIsolatedRoundTrip("suite-a-2");
            }
        }

        @Nested
        @Execution(ExecutionMode.CONCURRENT)
        class SuiteB {
            @Test
            void writeAndRead() throws Exception {
                assertIsolatedRoundTrip("suite-b");
            }

            @Test
            void writeAndReadAgain() throws Exception {
                assertIsolatedRoundTrip("suite-b-2");
            }
        }

        private void assertIsolatedRoundTrip(String marker) throws Exception {
            String id = UUID.randomUUID().toString();
            try (Connection c = database.getDataSource().getConnection();
                 Statement s = c.createStatement()) {
                s.executeUpdate("INSERT INTO some_table(id, foo) VALUES('" + id + "', '" + marker + "')");
            }
            try (Connection c = database.getDataSource().getConnection();
                 Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery("SELECT foo FROM some_table WHERE id='" + id + "'")) {
                assertThat(rs.next()).as("row must be visible on datasource from same extension call path").isTrue();
                assertThat(rs.getString(1)).isEqualTo(marker);
            }
        }
    }

    @Test
    void concurrentGetDataSourceOnSharedPerMethodExtension() throws Exception {
        DatabaseTestExtension database =
                new FlywayH2PostgreSQLFastTestExtension(DatabaseTestExtension.Mode.DATABASE_PER_TEST_METHOD);

        int threads = 32;
        int opsPerThread = 100;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger failures = new AtomicInteger();

        Future<?>[] futures = new Future<?>[threads];
        for (int t = 0; t < threads; t++) {
            futures[t] = pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < opsPerThread; i++) {
                        String id = UUID.randomUUID().toString();
                        // Two separate getDataSource() calls — must resolve to the same DB for this thread
                        try (Connection c = database.getDataSource().getConnection();
                             Statement s = c.createStatement()) {
                            s.executeUpdate("INSERT INTO some_table(id, foo) VALUES('" + id + "', 'x')");
                        }
                        try (Connection c = database.getDataSource().getConnection();
                             Statement s = c.createStatement();
                             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM some_table WHERE id='" + id + "'")) {
                            rs.next();
                            if (rs.getInt(1) != 1) {
                                failures.incrementAndGet();
                            }
                        }
                    }
                } catch (Exception e) {
                    failures.incrementAndGet();
                    e.printStackTrace();
                }
            });
        }
        start.countDown();
        for (Future<?> f : futures) {
            f.get();
        }
        pool.shutdownNow();
        assertThat(failures.get()).as("concurrent getDataSource races on shared extension").isZero();
    }
}
