package io.github.rieske.dbtest.extension.mysql;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.rieske.dbtest.extension.TestDatabase;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

class MysqlTestDatabase extends TestDatabase {
    private static final String DB_DUMP_FILENAME = "db_dump.sql";

    private final MySQLContainer<?> container;
    private final String jdbcPrefix;

    @SuppressWarnings("resource")
    MysqlTestDatabase(String version) {
        this.container = new MySQLContainer<>("mysql:" + version).withReuse(true);
        this.container.withTmpFs(Map.of("/var/lib/mysql", "rw"));
        this.container.withCommand("mysqld", "--innodb_flush_method=nosync");
        this.container.start();
        this.jdbcPrefix = "jdbc:mysql://%s:%s/".formatted(container.getHost(), container.getMappedPort(3306));
    }

    @Override
    protected void cloneTemplateDatabaseTo(String targetDatabaseName) {
        createDatabase(targetDatabaseName);
        restoreDatabase(targetDatabaseName);
    }

    @Override
    protected void migrateTemplateDatabase(Consumer<DataSource> migrator, DataSource templateDataSource) {
        migrator.accept(templateDataSource);
        dumpDatabase();
    }

    @Override
    protected DataSource dataSourceForDatabase(String databaseName) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(jdbcPrefix + databaseName);
        dataSource.setUser("root");
        dataSource.setPassword(container.getPassword());
        return dataSource;
    }

    @Override
    protected String getTemplateDatabaseName() {
        return container.getDatabaseName();
    }

    @Override
    protected DataSource getPrivilegedDataSource() {
        return dataSourceForDatabase(container.getDatabaseName());
    }

    private void dumpDatabase() {
        String command = "mysqldump -u root --password=%s %s > %s".formatted(container.getPassword(), getTemplateDatabaseName(), DB_DUMP_FILENAME);
        Container.ExecResult result = runInDatabaseContainer(command);
        if (result.getExitCode() != 0) {
            throw new RuntimeException("Error dumping database: " + result);
        }
    }

    private void restoreDatabase(String databaseName) {
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
