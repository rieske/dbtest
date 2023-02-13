package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.Nested;

import java.util.function.Function;

public abstract class SlowAndFastTests {
    private final String databaseVersion;
    private final Function<String, DatabaseTestExtension> slowExtensionProvider;
    private final Function<String, DatabaseTestExtension> fastExtensionProvider;

    public SlowAndFastTests(
            String databaseVersion,
            Function<String, DatabaseTestExtension> slowExtensionProvider,
            Function<String, DatabaseTestExtension> fastExtensionProvider
    ) {
        this.databaseVersion = databaseVersion;
        this.slowExtensionProvider = slowExtensionProvider;
        this.fastExtensionProvider = fastExtensionProvider;
    }

    @Nested
    class SlowDatabasePerTestMethodTest extends DatabasePerTestMethodTest {
        SlowDatabasePerTestMethodTest() {
            super(slowExtensionProvider.apply(databaseVersion));
        }
    }

    @Nested
    class FastDatabasePerTestMethodTest extends DatabasePerTestMethodTest {
        FastDatabasePerTestMethodTest() {
            super(fastExtensionProvider.apply(databaseVersion));
        }
    }

    @Nested
    class SlowTests extends DatabasePerformanceTest {
        SlowTests() {
            super(slowExtensionProvider.apply(databaseVersion));
        }
    }

    @Nested
    class FastTests extends DatabasePerformanceTest {
        FastTests() {
            super(fastExtensionProvider.apply(databaseVersion));
        }
    }
}
