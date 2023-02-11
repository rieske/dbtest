package io.github.rieske.dbtest.extension.mysql;

import org.flywaydb.core.Flyway;
import org.testcontainers.containers.Container;

import java.io.IOException;

public class MysqlFastTestExtension extends MysqlTestExtension {

    private static final String DB_DUMP_FILENAME = "db_dump.sql";

    static {
        Flyway.configure()
                .dataSource(dataSourceForDatabase(DB_CONTAINER.getDatabaseName()))
                .load()
                .migrate();
        dumpDatabase();
    }

    private static Container.ExecResult runInDatabaseContainer(String command) {
        try {
            return DB_CONTAINER.execInContainer("bash", "-c", command);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error running command in database container: " + command, e);
        }
    }

    private static void dumpDatabase() {
        String command = "mysqldump -u root --password=%s %s > %s".formatted(DB_CONTAINER.getPassword(), DB_CONTAINER.getDatabaseName(), DB_DUMP_FILENAME);
        Container.ExecResult result = runInDatabaseContainer(command);
        if (result.getExitCode() != 0) {
            throw new RuntimeException("Error dumping database: " + result);
        }
    }

    private void restoreDatabase() {
        String command = "mysql -u root --password=%s %s < %s".formatted(DB_CONTAINER.getPassword(), databaseName, DB_DUMP_FILENAME);
        Container.ExecResult result = runInDatabaseContainer(command);
        if (result.getExitCode() != 0) {
            throw new RuntimeException("Error restoring database: " + result);
        }
    }

    @Override
    protected void createFreshMigratedDatabase() {
        executeInDefaultDatabase("CREATE DATABASE " + databaseName);
        restoreDatabase();
    }
}
