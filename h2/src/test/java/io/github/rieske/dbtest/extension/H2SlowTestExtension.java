package io.github.rieske.dbtest.extension;

/**
 * Simulates the traditional approach of creating a fresh database and applying migrations for each test.
 */
abstract class H2SlowTestExtension extends H2TestExtension {
    H2SlowTestExtension(H2Mode h2Mode, DatabaseTestExtension.Mode dataRetentionMode) {
        super(h2Mode, dataRetentionMode, false);
    }
}
