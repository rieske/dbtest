package io.github.rieske.dbtest.extension;

import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.Map;
import java.util.function.Consumer;

class PostgreSQLTestDatabase extends TestDatabase {
    private final PostgreSQLContainer<?> container;
    private final String jdbcPrefix;

    @SuppressWarnings("resource")
    PostgreSQLTestDatabase(String version) {
        this.container = new PostgreSQLContainer<>("postgres:" + version).withReuse(true);
        this.container.withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));
        this.container.start();
        this.jdbcPrefix = "jdbc:postgresql://" + container.getHost() + ":" + container.getMappedPort(5432) + "/";
    }

    @Override
    void cloneTemplateDatabaseTo(String targetDatabaseName) {
        executePrivileged("CREATE DATABASE " + targetDatabaseName + " TEMPLATE " + getTemplateDatabaseName());
    }

    @Override
    void migrateTemplateDatabase(Consumer<DataSource> migrator, DataSource templateDataSource) {
        migrator.accept(dataSourceForDatabase(getTemplateDatabaseName()));
    }

    @Override
    DataSource dataSourceForDatabase(String databaseName) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(jdbcPrefix + databaseName);
        dataSource.setUser(container.getUsername());
        dataSource.setPassword(container.getPassword());
        return dataSource;
    }

    @Override
    String getTemplateDatabaseName() {
        return container.getDatabaseName();
    }

    @Override
    DataSource getPrivilegedDataSource() {
        return dataSourceForDatabase("postgres");
    }
}