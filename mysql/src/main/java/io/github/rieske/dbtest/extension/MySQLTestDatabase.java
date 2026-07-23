package io.github.rieske.dbtest.extension;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.function.Consumer;

class MySQLTestDatabase extends DatabaseEngine {
    private static final Logger log = LoggerFactory.getLogger(MySQLTestDatabase.class);

    private final MySQLContainer<?> container;
    private final String jdbcPrefix;
    private final String password;
    private volatile String cachedDumpSql;

    @SuppressWarnings("resource")
    MySQLTestDatabase(String version) {
        String dockerImageName = "mysql:" + version;
        this.container = new MySQLContainer<>(dockerImageName).withReuse(true);
        this.container.withTmpFs(Map.of("/var/lib/mysql", "rw"));
        this.container.withCommand(
                "mysqld",
                "--innodb-flush-method=nosync",
                "--sync-binlog=0",
                "--innodb-doublewrite=0",
                "--innodb-flush-log-at-trx-commit=0",
                "--skip-log-bin",
                "--performance-schema=OFF",
                "--skip-name-resolve",
                "--max-connections=500",
                "--table-definition-cache=800",
                "--table-open-cache=800"
        );
        long startTime = System.currentTimeMillis();
        log.info("Starting {} container", dockerImageName);
        this.container.start();
        log.info("Started {} container in {}", dockerImageName, TimeUtils.durationSince(startTime));
        this.jdbcPrefix = "jdbc:mysql://" + container.getHost() + ":" + container.getMappedPort(3306) + "/";
        this.password = container.getPassword();
        disableRedoLog();
    }

    private void disableRedoLog() {
        // Ephemeral test containers do not need crash recovery; skipping redo speeds DDL/clone.
        // Supported since MySQL 8.0.21 — best-effort so older images still work.
        try (Connection conn = dataSourceForDatabase(container.getDatabaseName()).getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER INSTANCE DISABLE INNODB REDO_LOG");
            log.info("Disabled InnoDB redo log for test container");
        } catch (SQLException e) {
            log.warn("Could not disable InnoDB redo log (requires MySQL 8.0.21+): {}", e.getMessage());
        }
    }

    @Override
    void cloneTemplateDatabaseTo(String targetDatabaseName) {
        createDatabase(targetDatabaseName);
        restoreDatabaseViaJdbc(targetDatabaseName);
    }

    @Override
    void migrateTemplateDatabase(Consumer<DataSource> migrator, DataSource templateDataSource) {
        migrator.accept(templateDataSource);
        cachedDumpSql = dumpDatabaseToString();
    }

    @Override
    DataSource dataSourceForDatabase(String databaseName) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(jdbcPrefix + databaseName);
        dataSource.setUser("root");
        dataSource.setPassword(password);
        return dataSource;
    }

    @Override
    String getTemplateDatabaseName() {
        return container.getDatabaseName();
    }

    @Override
    DataSource getPrivilegedDataSource() {
        return dataSourceForDatabase(container.getDatabaseName());
    }

    private String dumpDatabaseToString() {
        try {
            Container.ExecResult result = container.execInContainer(
                    "mysqldump",
                    "-u", "root",
                    "--password=" + password,
                    "--single-transaction",
                    "--skip-comments",
                    "--compact",
                    getTemplateDatabaseName()
            );
            if (result.getExitCode() != 0) {
                throw new RuntimeException("Error dumping database: " + result.getStderr());
            }
            return result.getStdout();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error dumping database", e);
        }
    }

    private void restoreDatabaseViaJdbc(String databaseName) {
        String dump = cachedDumpSql;
        if (dump == null || dump.isEmpty()) {
            throw new IllegalStateException("Template database dump is not available");
        }
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(jdbcPrefix + databaseName + "?allowMultiQueries=true&useLocalSessionState=true");
        dataSource.setUser("root");
        dataSource.setPassword(password);
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(dump);
        } catch (SQLException e) {
            throw new RuntimeException("Error restoring database " + databaseName, e);
        }
    }
}
