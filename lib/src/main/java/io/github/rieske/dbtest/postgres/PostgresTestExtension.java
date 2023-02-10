package io.github.rieske.dbtest.postgres;

import io.github.rieske.dbtest.DatabaseTestExtension;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public abstract class PostgresTestExtension extends DatabaseTestExtension implements BeforeEachCallback, AfterEachCallback {
    protected static final PostgreSQLContainer<?> DB_CONTAINER =
            new PostgreSQLContainer<>("postgres:14.4-alpine").withReuse(true);
    private static final String JDBC_URI;

    static {
        DB_CONTAINER.withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));
        DB_CONTAINER.start();
        JDBC_URI = "jdbc:postgresql://" + DB_CONTAINER.getHost() + ":" + DB_CONTAINER.getMappedPort(5432);
    }

    protected final String databaseName = "testdb_" + UUID.randomUUID().toString().replace('-', '_');

    @Override
    public void beforeEach(ExtensionContext context) {
        createFreshMigratedDatabase();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        executeInPostgresSchema("DROP DATABASE " + databaseName);
    }

    @Override
    public DataSource getDataSource() {
        return dataSourceForDatabase(databaseName);
    }

    protected static DataSource dataSourceForDatabase(String databaseName) {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUrl(JDBC_URI + "/" + databaseName);
        dataSource.setUser(DB_CONTAINER.getUsername());
        dataSource.setPassword(DB_CONTAINER.getPassword());
        return dataSource;
    }

    protected void executeInPostgresSchema(String sql) {
        var dataSource = dataSourceForDatabase("postgres");
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
