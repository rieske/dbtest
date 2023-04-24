package io.github.rieske.dbtest.extension;

/**
 * H2 compatibility mode
 */
public enum H2Mode {
    /**
     * Emulate PostgreSQL
     */
    POSTGRESQL("PostgreSQL"),
    /**
     * Emulate MySQL
     */
    MYSQL("MySQL");

    final String connectionStringValue;

    H2Mode(String connectionStringValue) {
        this.connectionStringValue = connectionStringValue;
    }
}
