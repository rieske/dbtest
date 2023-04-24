package io.github.rieske.dbtest.extension;

import io.github.rieske.dbtest.FlywayMigrator;

import javax.sql.DataSource;

public class FlywayH2PostgreSQLSlowTestExtension extends H2SlowTestExtension {
    public FlywayH2PostgreSQLSlowTestExtension(Mode mode) {
        super(H2Mode.POSTGRESQL, mode);
    }

    @Override
    protected void migrateDatabase(DataSource dataSource) {
        FlywayMigrator.migrateDatabase(dataSource);
    }
}
