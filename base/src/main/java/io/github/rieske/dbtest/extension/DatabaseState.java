package io.github.rieske.dbtest.extension;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

interface DatabaseState {
    String ensureDatabaseCreated(Class<?> testClass, Consumer<String> databaseCreator);

    static String newDatabaseName() {
        return "testdb_" + UUID.randomUUID().toString().replace('-', '_');
    }

    class PerMethod implements DatabaseState {
        @Override
        public String ensureDatabaseCreated(Class<?> testClass, Consumer<String> databaseCreator) {
            String databaseName = newDatabaseName();
            databaseCreator.accept(databaseName);
            return databaseName;
        }
    }

    class PerClass implements DatabaseState {
        private final Map<Class<?>, String> perClassDatabases = new ConcurrentHashMap<>();

        @Override
        public String ensureDatabaseCreated(Class<?> testClass, Consumer<String> databaseCreator) {
            String perClassDatabaseName = perClassDatabases.get(testClass);
            if (perClassDatabaseName == null) {
                synchronized (perClassDatabases) {
                    perClassDatabaseName = perClassDatabases.get(testClass);
                    if (perClassDatabaseName == null) {
                        perClassDatabaseName = newDatabaseName();
                        databaseCreator.accept(perClassDatabaseName);
                        perClassDatabases.put(testClass, perClassDatabaseName);
                    }
                }
            }
            return perClassDatabaseName;
        }
    }

    class PerExecution implements DatabaseState {
        private final String perExecutionDatabaseName = newDatabaseName();
        private volatile boolean perExecutionDatabaseCreated = false;

        @Override
        public String ensureDatabaseCreated(Class<?> testClass, Consumer<String> databaseCreator) {
            if (!perExecutionDatabaseCreated) {
                synchronized (perExecutionDatabaseName) {
                    if (!perExecutionDatabaseCreated) {
                        databaseCreator.accept(perExecutionDatabaseName);
                        perExecutionDatabaseCreated = true;
                    }
                }
            }
            return perExecutionDatabaseName;
        }
    }
}
