package io.github.rieske.dbtest.extension.postgresql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class PostgreSQLTestDatabaseManager {
    private static final Map<String, PostgreSQLTestDatabase> DATABASES = new ConcurrentHashMap<>();

    static PostgreSQLTestDatabase getDatabase(String version) {
        return DATABASES.computeIfAbsent(version, PostgreSQLTestDatabase::new);
    }
}
