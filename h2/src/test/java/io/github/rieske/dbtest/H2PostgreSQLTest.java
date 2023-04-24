package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.FlywayH2PostgreSQLFastTestExtension;
import io.github.rieske.dbtest.extension.FlywayH2PostgreSQLSlowTestExtension;

public interface H2PostgreSQLTest {
    default DatabaseTestExtension slowExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayH2PostgreSQLSlowTestExtension(mode);
    }

    default DatabaseTestExtension fastExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayH2PostgreSQLFastTestExtension(mode);
    }
}
