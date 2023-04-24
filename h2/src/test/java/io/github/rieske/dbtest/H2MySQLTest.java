package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import io.github.rieske.dbtest.extension.FlywayH2MySQLFastTestExtension;
import io.github.rieske.dbtest.extension.FlywayH2MySQLSlowTestExtension;

public interface H2MySQLTest {
    default DatabaseTestExtension slowExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayH2MySQLSlowTestExtension(mode);
    }

    default DatabaseTestExtension fastExtension(DatabaseTestExtension.Mode mode) {
        return new FlywayH2MySQLFastTestExtension(mode);
    }
}
