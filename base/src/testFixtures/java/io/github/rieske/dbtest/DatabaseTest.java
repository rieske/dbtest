package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

abstract class DatabaseTest {
    @RegisterExtension
    private final DatabaseTestExtension database;

    DatabaseTest(DatabaseTestExtension database) {
        this.database = database;
    }

    protected void executeUpdateSql(String sql) {
        try (Connection connection = database.getDataSource().getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T executeQuerySql(String sql, ResultSetMapper<T> resultSetMapper) {
        try (Connection connection = database.getDataSource().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return resultSetMapper.map(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
