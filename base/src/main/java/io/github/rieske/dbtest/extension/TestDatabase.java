package io.github.rieske.dbtest.extension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

abstract class TestDatabase {
    final DatabaseState perMethod = new DatabaseState.PerMethod(this);
    final DatabaseState perClass = new DatabaseState.PerClass(this);
    final DatabaseState perExecution = new DatabaseState.PerExecution(this);

    private volatile boolean templateDatabaseMigrated = false;

    void ensureTemplateDatabaseMigrated(Consumer<DataSource> migrator) {
        if (!templateDatabaseMigrated) {
            synchronized (this) {
                if (!templateDatabaseMigrated) {
                    migrateTemplateDatabase(migrator, dataSourceForDatabase(getTemplateDatabaseName()));
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
