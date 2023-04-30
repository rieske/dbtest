package io.github.rieske.dbtest.extension;

import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.Map;
import java.util.function.Consumer;

class PostgreSQLTestDatabase extends DatabaseEngine {
    private static final Logger log = LoggerFactory.getLogger(PostgreSQLTestDatabase.class);
    private final PostgreSQLContainer<?> container;
    private final String jdbcPrefix;

    @SuppressWarnings("resource")
    PostgreSQLTestDatabase(String version) {
        String dockerImageName = "postgres:" + version;
        this.container = new PostgreSQLContainer<>(dockerImageName).withReuse(true);
        this.container.setCommand(
                "postgres",
                "-c", "fsync=off",
                "-c", "full_page_writes=off"
        );
        this.container.withTmpFs(Map.of("/var/lib/postgresql/data", "rw"));
        long startTime = System.currentTimeMillis();
        log.info("Starting {} container", dockerImageName);
        this.container.start();
        log.info("Started {} container in {}", dockerImageName, TimeUtils.durationSince(startTime));
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
