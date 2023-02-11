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
    class SlowTests extends DatabaseTest {
        SlowTests() {
            super(slowExtensionProvider.apply(databaseVersion));
        }
    }

    @Nested
    class FastTests extends DatabaseTest {
        FastTests() {
            super(fastExtensionProvider.apply(databaseVersion));
        }
    }
}
