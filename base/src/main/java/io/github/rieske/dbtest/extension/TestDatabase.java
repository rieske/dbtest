package io.github.rieske.dbtest.extension;

class TestDatabase {
    private final DatabaseState perMethod;
    private final DatabaseState perClass;
    private final DatabaseState perExecution;

    TestDatabase(DatabaseEngine databaseEngine) {
        this.perMethod = new DatabaseState.PerMethod(databaseEngine);
        this.perClass = new DatabaseState.PerClass(databaseEngine);
        this.perExecution = new DatabaseState.PerExecution(databaseEngine);
    }

    DatabaseState getState(DatabaseTestExtension.Mode mode) {
        switch (mode) {
            case DATABASE_PER_TEST_METHOD:
                return perMethod;
            case DATABASE_PER_TEST_CLASS:
                return perClass;
            case DATABASE_PER_EXECUTION:
                return perExecution;
            default:
                throw new IllegalStateException("No database state strategy exists for " + mode + " mode");
        }
    }
}
