package io.github.rieske.dbtest.extension.postgres;

import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

class PostgresTestDatabase {
    private static final PostgreSQLContainer<?> DB_CONTAINER =
            new PostgreSQLContainer<>("postgres:14.4-alpine").withReuse(true);
    private static final String JDBC_URI;

    static {
        DB_CONTAINER.withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));
        DB_CONTAINER.start();
        JDBC_URI = "jdbc:postgresql://%s:%s/".formatted(DB_CONTAINER.getHost(), DB_CONTAINER.getMappedPort(5432));
    }

    String getTemplateDatabaseName() {
        return DB_CONTAINER.getDatabaseName();
    }

    DataSource dataSourceForDatabase(String databaseName) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(JDBC_URI + databaseName);
        dataSource.setUser(DB_CONTAINER.getUsername());
        dataSource.setPassword(DB_CONTAINER.getPassword());
        return dataSource;
    }

    void executeInPostgresSchema(String sql) {
        DataSource dataSource = dataSourceForDatabase("postgres");
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
