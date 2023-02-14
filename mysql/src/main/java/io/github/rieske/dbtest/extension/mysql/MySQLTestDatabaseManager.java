package io.github.rieske.dbtest.extension.mysql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MySQLTestDatabaseManager {
    private static final Map<String, MySQLTestDatabase> DATABASES = new ConcurrentHashMap<>();

    static MySQLTestDatabase getDatabase(String version) {
        return DATABASES.computeIfAbsent(version, MySQLTestDatabase::new);
    }
}
