package fr.siamois.infrastructure.database.initializer;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Order(6)
public class HistoryConstraintDropInitializer implements DatabaseInitializer {

    private final HikariDataSource hikariDataSource;

    public HistoryConstraintDropInitializer(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public void initialize() throws DatabaseDataInitException {
        try (Connection connection = hikariDataSource.getConnection()) {
            dropConstraintOfAllHistoryTables(connection);
        } catch (SQLException e) {
            log.error("History trigger error", e);
            throw new DatabaseDataInitException("History trigger error", e);
        }
    }

    private void dropConstraintOfAllHistoryTables(Connection connection) throws SQLException {
        List<String> tablesToStore = HistoryTriggerInitializer.TABLES_TO_STORE;
        try (Statement dropConstraintStatement = connection.createStatement()) {
            int constraintCount = 0;
            for (String tableName : tablesToStore) {
                constraintCount += addConstraintDropToBatch(connection, dropConstraintStatement,  "history_" + tableName);
            }
            if (constraintCount > 0) {
                dropConstraintStatement.executeBatch();
                log.info("{} history constraints dropped", constraintCount);
            }
        }
    }

    private int addConstraintDropToBatch(Connection connection, Statement dropConstraintStatement, String tableName) throws SQLException {
        List<String> constraints = findConstraintsOfTable(connection, tableName);
        for (String constraint : constraints) {
            dropConstraintStatement.addBatch(String.format("ALTER TABLE %s DROP CONSTRAINT %s", tableName, constraint));
            log.debug("Preparing drop of constraint {} from {}", constraint, tableName);
        }
        return constraints.size();
    }

    private List<String> findConstraintsOfTable(Connection connection, String tableName) throws SQLException {
        List<String> constraints = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(CONSTRAINT_QUERY)) {
            statement.setString(1, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    constraints.add(resultSet.getString(1));
                }
            }
        }
        return constraints;
    }

    private static final String CONSTRAINT_QUERY =
            """
                    SELECT con.conname
                    FROM pg_catalog.pg_constraint con
                             INNER JOIN pg_catalog.pg_class rel ON rel.oid = con.conrelid
                             INNER JOIN pg_catalog.pg_namespace nsp ON nsp.oid = connamespace
                    WHERE nsp.nspname = 'public'
                      AND rel.relname = ?
                      AND con.contype != 'p'
                    """;

}
