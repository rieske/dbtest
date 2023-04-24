package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.FlywayH2FastTestExtension;

public interface H2Test {
    default DatabaseTestExtension slowExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayH2FastTestExtension(mode);
    }

    default DatabaseTestExtension fastExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayH2FastTestExtension(mode);
    }
}
