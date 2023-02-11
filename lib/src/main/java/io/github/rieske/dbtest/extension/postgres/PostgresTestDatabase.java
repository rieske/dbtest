package io.github.rieske.dbtest.extension.postgres;

import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;

class PostgresTestDatabase {
    private final PostgreSQLContainer<?> container;
    private final String jdbcPrefix;

    private volatile boolean templateDatabaseMigrated = false;

    PostgresTestDatabase(String version) {
        this.container = new PostgreSQLContainer<>("postgres:" + version).withReuse(true);
        this.container.withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));
        this.container.start();
        this.jdbcPrefix = "jdbc:postgresql://%s:%s/".formatted(container.getHost(), container.getMappedPort(5432));
    }

    void migrateTemplate(Consumer<DataSource> migrator) {
        if (templateDatabaseMigrated) {
            return;
        }
        synchronized (this) {
            if (templateDatabaseMigrated) {
                return;
            }
            migrator.accept(dataSourceForDatabase(getTemplateDatabaseName()));
            templateDatabaseMigrated = true;
        }
    }

    String getTemplateDatabaseName() {
        return container.getDatabaseName();
    }

    DataSource dataSourceForDatabase(String databaseName) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(jdbcPrefix + databaseName);
        dataSource.setUser(container.getUsername());
        dataSource.setPassword(container.getPassword());
        return dataSource;
    }

    void executePrivileged(String sql) {
        DataSource dataSource = dataSourceForDatabase("postgres");
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
