package io.github.rieske.dbtest;

interface MySQLTest {
    default String mysqlVersion() {
        return Environment.getEnvOrDefault("MYSQL_VERSION", "8.0.32");
    }
}
