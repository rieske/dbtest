package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class PerformanceTests {
    private final DatabaseTestExtension slowExtension;
    private final DatabaseTestExtension fastExtension;

    public PerformanceTests(DatabaseTestExtension slowExtension, DatabaseTestExtension fastExtension) {
        this.slowExtension = slowExtension;
        this.fastExtension = fastExtension;
    }

    @Nested
    class SlowTests extends TestTemplate {
        SlowTests() {
            super(slowExtension);
        }
    }

    @Nested
    class FastTests extends TestTemplate {
        FastTests() {
            super(fastExtension);
        }
    }

    abstract static class TestTemplate extends DatabaseTest {
        private static final int REPETITIONS = 100;

        TestTemplate(DatabaseTestExtension database) {
            super(database);
        }

        @RepeatedTest(REPETITIONS)
        void doNothing() {
        }

        @RepeatedTest(REPETITIONS)
        void interactWithDatabase() {
            UUID id = UUID.randomUUID();
            String foo = UUID.randomUUID().toString();
            executeUpdateSql("INSERT INTO some_table(id, foo) VALUES('" + id + "', '" + foo + "')");

            int recordCount = getRecordCount();
            assertThat(recordCount).isGreaterThanOrEqualTo(1);

            executeQuerySql("SELECT * FROM some_table WHERE id='" + id + "'", rs -> {
                assertThat(rs.next()).isTrue();
                assertThat(UUID.fromString(rs.getString(1))).isEqualTo(id);
                assertThat(rs.getString(2)).isEqualTo(foo);
                return null;
            });
        }
    }
}
