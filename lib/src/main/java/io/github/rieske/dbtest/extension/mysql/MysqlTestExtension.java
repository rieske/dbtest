package io.github.rieske.dbtest.extension.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.rieske.dbtest.DatabaseTestExtension;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public abstract class MysqlTestExtension extends DatabaseTestExtension implements BeforeEachCallback, AfterEachCallback {
    protected static final MySQLContainer<?> DB_CONTAINER =
            new MySQLContainer<>("mysql:8.0.32").withReuse(true);
    private static final String JDBC_URI;

    static {
        DB_CONTAINER.withTmpFs(Map.of("/var/lib/mysql", "rw"));
        DB_CONTAINER.withCommand("mysqld", "--innodb_flush_method=nosync");
        DB_CONTAINER.start();
        JDBC_URI = "jdbc:mysql://" + DB_CONTAINER.getHost() + ":" + DB_CONTAINER.getMappedPort(3306);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        createFreshMigratedDatabase();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        executeInDefaultDatabase("DROP DATABASE " + databaseName);
    }

    @Override
    public DataSource getDataSource() {
        return dataSourceForDatabase(databaseName);
    }

    protected static DataSource dataSourceForDatabase(String databaseName) {
        var dataSource = new MysqlDataSource();
        dataSource.setUrl(JDBC_URI + "/" + databaseName);
        dataSource.setUser("root");
        dataSource.setPassword(DB_CONTAINER.getPassword());
        return dataSource;
    }

    protected void executeInDefaultDatabase(String sql) {
        var dataSource = dataSourceForDatabase(DB_CONTAINER.getDatabaseName());
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
