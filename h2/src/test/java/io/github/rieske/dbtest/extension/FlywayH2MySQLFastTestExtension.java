package io.github.rieske.dbtest.extension;

import io.github.rieske.dbtest.FlywayMigrator;

import javax.sql.DataSource;

public class FlywayH2MySQLFastTestExtension extends H2FastTestExtension {
    public FlywayH2MySQLFastTestExtension(Mode mode) {
        super(H2Mode.MYSQL, mode);
    }

    @Override
    protected void migrateDatabase(DataSource dataSource) {
        FlywayMigrator.migrateDatabase(dataSource);
    }
}
