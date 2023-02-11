package io.github.rieske.dbtest.extension.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

class MysqlTestDatabase {
    private static final MySQLContainer<?> DB_CONTAINER =
            new MySQLContainer<>("mysql:8.0.32").withReuse(true);
    private static final String JDBC_URI;

    static {
        DB_CONTAINER.withTmpFs(Map.of("/var/lib/mysql", "rw"));
        DB_CONTAINER.withCommand("mysqld", "--innodb_flush_method=nosync");
        DB_CONTAINER.start();
        JDBC_URI = "jdbc:mysql://%s:%s/".formatted(DB_CONTAINER.getHost(), DB_CONTAINER.getMappedPort(3306));
    }

    String getTemplateDatabaseName() {
        return DB_CONTAINER.getDatabaseName();
    }

    DataSource dataSourceForDatabase(String databaseName) {
        var dataSource = new MysqlDataSource();
        dataSource.setUrl(JDBC_URI + databaseName);
        dataSource.setUser("root");
        dataSource.setPassword(DB_CONTAINER.getPassword());
        return dataSource;
    }

    void executeInDefaultDatabase(String sql) {
        var dataSource = dataSourceForDatabase(DB_CONTAINER.getDatabaseName());
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dumpDatabase(String dbDumpFilename) {
        String command = "mysqldump -u root --password=%s %s > %s".formatted(DB_CONTAINER.getPassword(), getTemplateDatabaseName(), dbDumpFilename);
        Container.ExecResult result = runInDatabaseContainer(command);
        if (result.getExitCode() != 0) {
            throw new RuntimeException("Error dumping database: " + result);
        }
    }

    void restoreDatabase(String databaseName, String dbDumpFilename) {
        String command = "mysql -u root --password=%s %s < %s".formatted(DB_CONTAINER.getPassword(), databaseName, dbDumpFilename);
        Container.ExecResult result = runInDatabaseContainer(command);
        if (result.getExitCode() != 0) {
            throw new RuntimeException("Error restoring database: " + result);
        }
    }

    private static Container.ExecResult runInDatabaseContainer(String command) {
        try {
            return DB_CONTAINER.execInContainer("bash", "-c", command);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error running command in database container: " + command, e);
        }
    }
}
