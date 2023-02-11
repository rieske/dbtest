package io.github.rieske.dbtest.extension.mysql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MysqlTestDatabaseManager {
    private static final Map<String, MysqlTestDatabase> DATABASES = new ConcurrentHashMap<>();

    static MysqlTestDatabase getDatabase(String version) {
        return DATABASES.computeIfAbsent(version, MysqlTestDatabase::new);
    }
}
