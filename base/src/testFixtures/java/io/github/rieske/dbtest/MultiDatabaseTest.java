package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

abstract class MultiDatabaseTest {
    @RegisterExtension
    private final DatabaseTestExtension db1;

    @RegisterExtension
    private final DatabaseTestExtension db2;

    MultiDatabaseTest(DatabaseTestExtension db1, DatabaseTestExtension db2) {
        this.db1 = db1;
        this.db2 = db2;
    }

    @Test
    void insertIntoTwoDatabases() {
        insertRandomRecord(db1);
        insertRandomRecord(db2);
    }

    protected void insertRandomRecord(DatabaseTestExtension db) {
        UUID id = UUID.randomUUID();
        String foo = UUID.randomUUID().toString();
        executeUpdateSql(db, "INSERT INTO some_table(id, foo) VALUES('" + id + "', '" + foo + "')");
    }

    protected void executeUpdateSql(DatabaseTestExtension db, String sql) {
        try (Connection connection = db.getDataSource().getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
