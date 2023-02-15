package io.github.rieske.dbtest.extension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public abstract class TestDatabase {
    private volatile boolean templateDatabaseMigrated = false;

    public void migrateTemplateDatabase(Consumer<DataSource> migrator) {
        if (templateDatabaseMigrated) {
            return;
        }
        synchronized (this) {
            if (templateDatabaseMigrated) {
                return;
            }
            migrateTemplateDatabase(migrator, dataSourceForDatabase(getTemplateDatabaseName()));
            templateDatabaseMigrated = true;
        }
    }

    public void createDatabase(String databaseName) {
        executePrivileged("CREATE DATABASE " + databaseName);
    }

    public void dropDatabase(String databaseName) {
        executePrivileged("DROP DATABASE " + databaseName);
    }

    public abstract void cloneTemplateDatabaseTo(String targetDatabaseName);

    protected void executePrivileged(String sql) {
        DataSource dataSource = getPrivilegedDataSource();
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract DataSource dataSourceForDatabase(String databaseName);

    protected abstract String getTemplateDatabaseName();

    protected abstract DataSource getPrivilegedDataSource();

    protected abstract void migrateTemplateDatabase(Consumer<DataSource> migrator, DataSource templateDataSource);
}
