package io.github.rieske.dbtest.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class H2TestDatabaseManager {
    private static final Map<H2Mode, TestDatabase> DATABASES = new ConcurrentHashMap<>();

    static TestDatabase getDatabase(H2Mode dialect) {
        // TODO: pass dialect on to H2
        return DATABASES.computeIfAbsent(dialect, d -> new TestDatabase(new H2TestDatabase()));
    }
}
