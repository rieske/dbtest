package io.github.rieske.dbtest.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

abstract class DatabaseEngine {
    private static final Logger log = LoggerFactory.getLogger(DatabaseEngine.class);
    private volatile boolean templateDatabaseMigrated = false;

    void ensureTemplateDatabaseMigrated(Consumer<DataSource> migrator) {
        if (!templateDatabaseMigrated) {
            synchronized (this) {
                if (!templateDatabaseMigrated) {
                    long startTime = System.currentTimeMillis();
                    log.info("Migrating template database");
                    migrateTemplateDatabase(migrator, dataSourceForDatabase(getTemplateDatabaseName()));
                    log.info("Migrated template database in {}", TimeUtils.durationSince(startTime));
                    templateDatabaseMigrated = true;
                }
            }
        }
    }

    void createDatabase(String databaseName) {
        executePrivileged("CREATE DATABASE " + databaseName);
    }

    void dropDatabase(String databaseName) {
        executePrivileged("DROP DATABASE " + databaseName);
    }

    abstract void cloneTemplateDatabaseTo(String targetDatabaseName);

    void executePrivileged(String sql) {
        DataSource dataSource = getPrivilegedDataSource();
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    abstract DataSource dataSourceForDatabase(String databaseName);

    abstract String getTemplateDatabaseName();

    abstract DataSource getPrivilegedDataSource();

    abstract void migrateTemplateDatabase(Consumer<DataSource> migrator, DataSource templateDataSource);
}
