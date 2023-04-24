package io.github.rieske.dbtest.extension;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

class H2TestDatabase extends DatabaseEngine {
    private static final class Database {
        final DataSource dataSource;
        final Connection connection;

        private Database(DataSource dataSource, Connection connection) {
            this.dataSource = dataSource;
            this.connection = connection;
        }
    }

    private final H2Mode h2Mode;
    private final DataSource templateDataSource;
    private final Map<String, Database> databases = new ConcurrentHashMap<>();
    private Consumer<DataSource> migrator;


    H2TestDatabase(H2Mode h2Mode) {
        this.h2Mode = h2Mode;
        this.templateDataSource = dataSourceForDatabase(getTemplateDatabaseName());
    }

    @Override
    void cloneTemplateDatabaseTo(String targetDatabaseName) {
        migrator.accept(dataSourceForDatabase(targetDatabaseName));
    }

    @Override
    DataSource dataSourceForDatabase(String databaseName) {
        if (!databases.containsKey(databaseName)) {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setUrl("jdbc:h2:mem:" + databaseName + ";DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=1;MODE=" + h2Mode.connectionStringValue);
            try {
                databases.put(databaseName, new Database(dataSource, dataSource.getConnection()));
            } catch (SQLException e) {
                throw new RuntimeException("Could not acquire a connection", e);
            }
        }
        return databases.get(databaseName).dataSource;
    }

    @Override
    String getTemplateDatabaseName() {
        return "template";
    }

    @Override
    DataSource getPrivilegedDataSource() {
        return templateDataSource;
    }

    @Override
    void migrateTemplateDatabase(Consumer<DataSource> migrator, DataSource templateDataSource) {
        this.migrator = migrator;
        migrator.accept(templateDataSource);
    }

    @Override
    void createDatabase(String databaseName) {
    }

    @Override
    void dropDatabase(String databaseName) {
        Database database = databases.get(databaseName);
        if (database != null) {
            try {
                database.connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Could not close a connection", e);
            }
        }
    }
}
