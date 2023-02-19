package io.github.rieske.dbtest;

import org.assertj.core.api.Assertions;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class TestRepository {
    private final DataSource dataSource;

    public TestRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertRandomRecord() {
        UUID id = UUID.randomUUID();
        String foo = UUID.randomUUID().toString();
        executeUpdateSql("INSERT INTO some_table(id, foo) VALUES('" + id + "', '" + foo + "')");
    }

    public int getRecordCount() {
        return executeQuerySql("SELECT COUNT(*) FROM some_table", rs -> {
            Assertions.assertThat(rs.next()).isTrue();
            return rs.getInt(1);
        });
    }

    private void executeUpdateSql(String sql) {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T executeQuerySql(String sql, DatabaseTest.ResultSetMapper<T> resultSetMapper) {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return resultSetMapper.map(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
