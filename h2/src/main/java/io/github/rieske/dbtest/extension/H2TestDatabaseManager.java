package io.github.rieske.dbtest.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class H2TestDatabaseManager {
    private static final Map<H2Mode, TestDatabase> DATABASES = new ConcurrentHashMap<>();

    static TestDatabase getDatabase(H2Mode h2Mode) {
        return DATABASES.computeIfAbsent(h2Mode, d -> new TestDatabase(new H2TestDatabase(h2Mode)));
    }
}
