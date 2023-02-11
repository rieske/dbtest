package io.github.rieske.dbtest.extension;

import org.junit.jupiter.api.extension.Extension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public abstract class DatabaseTestExtension implements Extension {
    protected final String databaseName = "testdb_" + UUID.randomUUID().toString().replace('-', '_');

    public abstract DataSource getDataSource();

    public void executeUpdateSql(String sql) {
        try (Connection connection = getDataSource().getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T executeQuerySql(String sql, ResultSetMapper<T> resultSetMapper) {
        try (Connection connection = getDataSource().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return resultSetMapper.map(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void createFreshMigratedDatabase();

    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
