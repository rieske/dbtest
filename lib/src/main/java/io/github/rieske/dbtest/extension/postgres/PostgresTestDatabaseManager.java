package io.github.rieske.dbtest.extension.postgres;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class PostgresTestDatabaseManager {
    private static final Map<String, PostgresTestDatabase> DATABASES = new ConcurrentHashMap<>();

    static PostgresTestDatabase getDatabase(String version) {
        return DATABASES.computeIfAbsent(version, PostgresTestDatabase::new);
    }
}
