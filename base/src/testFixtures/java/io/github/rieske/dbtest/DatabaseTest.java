package io.github.rieske.dbtest;

import io.github.rieske.dbtest.extension.DatabaseTestExtension;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class DatabaseTest {
    @RegisterExtension
    protected final DatabaseTestExtension database;

    DatabaseTest(DatabaseTestExtension database) {
        this.database = database;
    }

    protected void insertRandomRecord() {
        UUID id = UUID.randomUUID();
        String foo = UUID.randomUUID().toString();
        executeUpdateSql("INSERT INTO some_table(id, foo) VALUES('" + id + "', '" + foo + "')");
    }

    protected int getRecordCount() {
        return executeQuerySql("SELECT COUNT(*) FROM some_table", rs -> {
            assertThat(rs.next()).isTrue();
            return rs.getInt(1);
        });
    }

    protected void assertRecordCount(int expectedCount) {
        assertThat(getRecordCount()).isEqualTo(expectedCount);
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
