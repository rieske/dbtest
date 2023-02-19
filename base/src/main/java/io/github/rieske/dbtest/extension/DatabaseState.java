package io.github.rieske.dbtest.extension;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

abstract class DatabaseState {
    protected final TestDatabase database;

    DatabaseState(TestDatabase database) {
        this.database = database;
    }

    abstract String ensureDatabaseCreated(String databaseName, Class<?> testClass, BiConsumer<TestDatabase, String> databaseCreator);
    abstract void afterTestMethod(String databaseName);

    private static String newDatabaseName() {
        return "testdb_" + UUID.randomUUID().toString().replace('-', '_');
    }

    static class PerMethod extends DatabaseState {
        PerMethod(TestDatabase database) {
            super(database);
        }

        @Override
        String ensureDatabaseCreated(String databaseName, Class<?> testClass, BiConsumer<TestDatabase, String> databaseCreator) {
            if (databaseName != null) {
                return databaseName;
            }
            databaseName = newDatabaseName();
            databaseCreator.accept(database, databaseName);
            return databaseName;
        }

        @Override
        void afterTestMethod(String databaseName) {
            if (databaseName != null) {
                database.dropDatabase(databaseName);
            }
        }
    }

    static class PerClass extends DatabaseState {
        private final Map<Class<?>, String> perClassDatabases = new ConcurrentHashMap<>();

        PerClass(TestDatabase database) {
            super(database);
        }

        @Override
        String ensureDatabaseCreated(String databaseName, Class<?> testClass, BiConsumer<TestDatabase, String> databaseCreator) {
            if (testClass == null) {
                throw new IllegalStateException("Per-class database extension must be registered as a static test class field in order to use the datasource during test instance construction.");
            }
            if (databaseName != null && databaseName.equals(perClassDatabases.get(testClass))) {
                return databaseName;
            }
            String perClassDatabaseName = perClassDatabases.get(testClass);
            if (perClassDatabaseName == null) {
                synchronized (perClassDatabases) {
                    perClassDatabaseName = perClassDatabases.get(testClass);
                    if (perClassDatabaseName == null) {
                        perClassDatabaseName = newDatabaseName();
                        databaseCreator.accept(database, perClassDatabaseName);
                        perClassDatabases.put(testClass, perClassDatabaseName);
                    }
                }
            }
            return perClassDatabaseName;
        }

        @Override
        void afterTestMethod(String databaseName) {
        }
    }

    static class PerExecution extends DatabaseState {
        private final String perExecutionDatabaseName = newDatabaseName();
        private volatile boolean perExecutionDatabaseCreated = false;

        PerExecution(TestDatabase database) {
            super(database);
        }

        @Override
        String ensureDatabaseCreated(String databaseName, Class<?> testClass, BiConsumer<TestDatabase, String> databaseCreator) {
            if (perExecutionDatabaseName.equals(databaseName)) {
                return databaseName;
            }
            if (!perExecutionDatabaseCreated) {
                synchronized (perExecutionDatabaseName) {
                    if (!perExecutionDatabaseCreated) {
                        databaseCreator.accept(database, perExecutionDatabaseName);
                        perExecutionDatabaseCreated = true;
                    }
                }
            }
            return perExecutionDatabaseName;
        }

        @Override
        void afterTestMethod(String databaseName) {
        }
    }
}
