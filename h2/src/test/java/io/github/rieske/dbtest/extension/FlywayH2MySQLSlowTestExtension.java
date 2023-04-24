package io.github.rieske.dbtest.extension;

import io.github.rieske.dbtest.FlywayMigrator;

import javax.sql.DataSource;

public class FlywayH2MySQLSlowTestExtension extends H2SlowTestExtension {
    public FlywayH2MySQLSlowTestExtension(Mode mode) {
        super(H2Mode.MYSQL, mode);
    }

    @Override
    protected void migrateDatabase(DataSource dataSource) {
        FlywayMigrator.migrateDatabase(dataSource);
    }
}
