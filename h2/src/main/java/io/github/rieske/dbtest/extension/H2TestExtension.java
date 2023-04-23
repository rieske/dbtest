package io.github.rieske.dbtest.extension;

abstract class H2TestExtension extends DatabaseTestExtension {
    H2TestExtension(H2Mode dialect, Mode mode, boolean migrateOnce) {
        super(H2TestDatabaseManager.getDatabase(dialect), mode, migrateOnce);
    }
}
