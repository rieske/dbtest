package io.github.rieske.dbtest;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public final class FlywayMigrator {
    private FlywayMigrator() {
    }

    public static void migrateDatabase(DataSource dataSource) {
        Flyway.configure()
                .loggers("slf4j")
                .dataSource(dataSource)
                .load()
                .migrate();
    }
}
