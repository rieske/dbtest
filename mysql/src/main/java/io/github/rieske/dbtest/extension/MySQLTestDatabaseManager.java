package io.github.rieske.dbtest.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MySQLTestDatabaseManager {
    private static final Map<String, TestDatabase> DATABASES = new ConcurrentHashMap<>();

    static TestDatabase getDatabase(String version) {
        return DATABASES.computeIfAbsent(version, v -> new TestDatabase(new MySQLTestDatabase(v)));
    }
}
