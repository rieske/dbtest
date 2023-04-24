package io.github.rieske.dbtest.extension;

abstract class H2TestExtension extends DatabaseTestExtension {
    H2TestExtension(H2Mode h2mode, Mode dataRetentionMode, boolean migrateOnce) {
        super(H2TestDatabaseManager.getDatabase(h2mode), dataRetentionMode, migrateOnce);
    }
}
