package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;

import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class PerformanceTests {
    private final String databaseVersion;
    private final Function<String, DatabaseTestExtension> slowExtensionProvider;
    private final Function<String, DatabaseTestExtension> fastExtensionProvider;

    public PerformanceTests(
            String databaseVersion,
            Function<String, DatabaseTestExtension> slowExtensionProvider,
            Function<String, DatabaseTestExtension> fastExtensionProvider
    ) {
        this.databaseVersion = databaseVersion;
        this.slowExtensionProvider = slowExtensionProvider;
        this.fastExtensionProvider = fastExtensionProvider;
    }

    @Nested
    class SlowTests extends TestTemplate {
        SlowTests() {
            super(slowExtensionProvider.apply(databaseVersion));
        }
    }

    @Nested
    class FastTests extends TestTemplate {
        FastTests() {
            super(fastExtensionProvider.apply(databaseVersion));
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

            assertRecordCount(1);

            executeQuerySql("SELECT * FROM some_table", rs -> {
                assertThat(rs.next()).isTrue();
                assertThat(UUID.fromString(rs.getString(1))).isEqualTo(id);
                assertThat(rs.getString(2)).isEqualTo(foo);
                return null;
            });
        }
    }
}
