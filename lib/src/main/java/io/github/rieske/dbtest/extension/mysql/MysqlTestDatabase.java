package io.github.rieske.dbtest.extension.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;

class MysqlTestDatabase {
    private final MySQLContainer<?> container;
    private final String jdbcPrefix;

    private volatile boolean templateDatabaseMigrated = false;

    private static final String DB_DUMP_FILENAME = "db_dump.sql";

    MysqlTestDatabase(String version) {
        this.container = new MySQLContainer<>("mysql:" + version).withReuse(true);
        this.container.withTmpFs(Map.of("/var/lib/mysql", "rw"));
        this.container.withCommand("mysqld", "--innodb_flush_method=nosync");
        this.container.start();
        this.jdbcPrefix = "jdbc:mysql://%s:%s/".formatted(container.getHost(), container.getMappedPort(3306));
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
            this.dumpDatabase();
            templateDatabaseMigrated = true;
        }
    }

    String getTemplateDatabaseName() {
        return container.getDatabaseName();
    }

    DataSource dataSourceForDatabase(String databaseName) {
        var dataSource = new MysqlDataSource();
        dataSource.setUrl(jdbcPrefix + databaseName);
        dataSource.setUser("root");
        dataSource.setPassword(container.getPassword());
        return dataSource;
    }

    void executePrivileged(String sql) {
        var dataSource = dataSourceForDatabase(container.getDatabaseName());
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dumpDatabase() {
        String command = "mysqldump -u root --password=%s %s > %s".formatted(container.getPassword(), getTemplateDatabaseName(), DB_DUMP_FILENAME);
        Container.ExecResult result = runInDatabaseContainer(command);
        if (result.getExitCode() != 0) {
            throw new RuntimeException("Error dumping database: " + result);
        }
    }

    void restoreDatabase(String databaseName) {
        String command = "mysql -u root --password=%s %s < %s".formatted(container.getPassword(), databaseName, DB_DUMP_FILENAME);
        Container.ExecResult result = runInDatabaseContainer(command);
        if (result.getExitCode() != 0) {
            throw new RuntimeException("Error restoring database: " + result);
        }
    }

    private Container.ExecResult runInDatabaseContainer(String command) {
        try {
            return container.execInContainer("bash", "-c", command);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error running command in database container: " + command, e);
        }
    }
}
