package fr.siamois.infrastructure.database;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.models.exceptions.database.WrongTableNameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;

/**
 * Create all the history triggers and functions.
 *
 * @author Julien Linget
 */
@Slf4j
@Component
public class HistoryTriggerInitializer {

    private final HikariDataSource dataSource;

    public HistoryTriggerInitializer(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Create all history triggers.
     */
    public void createHistoryTriggers() {
        try {
            Connection connection = dataSource.getConnection();
            List<String> tablesToStore = List.of(
                    "action_unit",
                    "siamois_document",
                    "recording_unit",
                    "recording_unit_study",
                    "spatial_unit",
                    "specimen",
                    "specimen_study");

            for (String tableName : tablesToStore)
                createSQLHistTrigger(connection, tableName, "history_" + tableName);

            connection.close();
        } catch (SQLException e) {
            log.error("Could not create history triggers", e);
        }
    }

    public void createSQLHistTrigger(Connection connection, String tableName, String historyTableName) throws SQLException, WrongTableNameException {
        StringBuilder columnList = new StringBuilder();
        StringBuilder selectList = new StringBuilder();

        addColumnNamesToLists(connection, tableName, columnList, selectList);
        createHistoryFunction(connection, tableName, historyTableName, columnList, selectList);
        createHistoryTrigger(connection, tableName);
    }

    private void createHistoryTrigger(Connection connection, String tableName) throws SQLException {
        StringBuilder triggerBuilder = new StringBuilder();
        triggerBuilder.append("CREATE OR REPLACE TRIGGER trg_save_history_").append(tableName).append("\n")
                .append("AFTER UPDATE OR DELETE ON ").append(tableName).append("\n")
                .append("FOR EACH ROW EXECUTE FUNCTION save_history_").append(tableName).append("();\n");

        try {
            Statement triggerStatement = connection.createStatement();
            triggerStatement.execute(triggerBuilder.toString());
            triggerStatement.close();
        } catch (SQLException e) {
            log.trace(triggerBuilder.toString());
            throw e;
        }
    }

    private void createHistoryFunction(Connection connection, String tableName, String historyTableName, StringBuilder columnList, StringBuilder selectList) throws SQLException {
        StringBuilder functionBuilder = new StringBuilder();
        functionBuilder.append("CREATE OR REPLACE FUNCTION save_history_").append(tableName).append("() RETURNS TRIGGER AS \n")
                .append("$$\n")
                .append("BEGIN\n")
                .append("\t").append("INSERT INTO ").append(historyTableName)
                .append("(").append(columnList).append(", update_type, update_time)\n")
                .append("\t").append("SELECT ").append(selectList).append(", TG_OP, NOW();\n")
                .append("\t").append("RETURN NEW;\n")
                .append("END\n")
                .append("$$\n")
                .append("LANGUAGE plpgsql;\n");

        try {
            Statement functionStatement = connection.createStatement();
            functionStatement.execute(functionBuilder.toString());
            functionStatement.close();
        } catch (SQLException e) {
            log.trace(functionBuilder.toString());
            throw e;
        }
    }

    private void addColumnNamesToLists(Connection connection, String tableName, StringBuilder columnList, StringBuilder selectList) throws SQLException, WrongTableNameException {
        String query = "SELECT column_name FROM information_schema.columns WHERE table_name = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, tableName);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String columnName = resultSet.getString("column_name");
            columnList.append(columnName).append(", ");
            selectList.append("OLD.").append(columnName).append(", ");
        }

        if (columnList.isEmpty()) {
            throw new WrongTableNameException("Table with name " + tableName + " does not exist in the current database");
        } else {
            columnList.setLength(columnList.length() - 2);
            selectList.setLength(selectList.length() - 2);
        }
    }

}
