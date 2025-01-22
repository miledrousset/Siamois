package fr.siamois.infrastructure.repositories.history;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.models.Team;
import fr.siamois.models.TraceableEntity;
import fr.siamois.models.auth.Person;
import fr.siamois.models.history.GlobalHistoryEntry;
import fr.siamois.models.history.HistoryUpdateType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class GlobalHistoryRepository {

    private final HikariDataSource hikariDataSource;

    private static final List<String> existingTablenames = new ArrayList<>();

    public GlobalHistoryRepository(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
        populateTablenameList();
    }

    private void populateTablenameList() {
        if (!existingTablenames.isEmpty()) return;

        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                existingTablenames.add(resultSet.getString("table_name"));
            }
        } catch (SQLException e) {
            log.error("Error while populating table names", e);
            existingTablenames.clear();
        }
    }

    private boolean tablenameNotExist(String tablename) {
        return existingTablenames.stream()
                .filter((name) -> name.equalsIgnoreCase(tablename))
                .findFirst()
                .isEmpty();
    }

    private boolean isNotAForeignKey(String columnName) {
        columnName = columnName.toLowerCase();
        return !columnName.startsWith("fk_");
    }

    private boolean isNotHistoryKey(String columnName) {
        return !columnName.equalsIgnoreCase("history_id");
    }

    private boolean isIdKey(String columnName) {
        columnName = columnName.toLowerCase();
        return columnName.endsWith("id") || columnName.startsWith("id");
    }

    private String findColumnTableIdNameInResultSet(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                if (isIdKey(columnName) && isNotHistoryKey(columnName) && isNotAForeignKey(columnName)) {
                    return columnName;
                }
            }
        } catch (SQLException e) {
            log.error("Could not find ID column in history", e);
        }
        return "history_id";
    }

    public List<GlobalHistoryEntry> findAllHistoryOfUserBetween(String tableName, Person author, Team team, OffsetDateTime start, OffsetDateTime end) {
        List<GlobalHistoryEntry> entries = new ArrayList<>();

        if (tablenameNotExist(tableName)) {
            logNotExistTable(tableName);
            return entries;
        }

        try (Connection connection = hikariDataSource.getConnection()) {
            String query = "SELECT * FROM " + tableName +" WHERE fk_author_id = ? AND fk_team_id = ? AND  update_time BETWEEN ? AND ?";
            PreparedStatement statement = prepareQueryStatement(author, team, start, end, connection, query);

            ResultSet resultSet = statement.executeQuery();
            String idColumnName = findColumnTableIdNameInResultSet(resultSet);

            while (resultSet.next()) {
                HistoryUpdateType updateType = HistoryUpdateType.valueOf(resultSet.getString("update_type"));
                OffsetDateTime updateTime = resultSet.getObject("update_time", OffsetDateTime.class);
                Long tableId = resultSet.getLong(idColumnName);

                entries.add(new GlobalHistoryEntry(updateType, tableId, updateTime));
            }


        } catch (SQLException e) {
            log.error("Error while searching for HistoryEntry for table {}", tableName, e);
        }

        return entries;
    }

    private void logNotExistTable(String tableName) {
        log.error("Table name {} does not exist", tableName);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class LocalTraceableEntity extends TraceableEntity {
        private Long id;
    }

    public List<TraceableEntity> findAllCreationOfUserBetween(String tableName, Person author, Team team, OffsetDateTime start, OffsetDateTime end) {
        List<TraceableEntity> entries = new ArrayList<>();

        if (tablenameNotExist(tableName)) {
            logNotExistTable(tableName);
            return entries;
        }

        try (Connection connection = hikariDataSource.getConnection()) {
            String query = "SELECT * FROM " + tableName + " WHERE fk_author_id = ? AND fk_team_id = ? AND creation_time BETWEEN ? AND ?";
            PreparedStatement statement = prepareQueryStatement(author, team, start, end, connection, query);

            ResultSet resultSet = statement.executeQuery();
            String idColumnName = findColumnTableIdNameInResultSet(resultSet);

            while (resultSet.next()) {
                OffsetDateTime creationTime = resultSet.getObject("creation_time", OffsetDateTime.class);
                Long tableId = resultSet.getLong(idColumnName);

                LocalTraceableEntity entity = new LocalTraceableEntity();
                entity.setAuthor(author);
                entity.setCreationTime(creationTime);
                entity.setId(tableId);
                entity.setAuthorTeam(team);

                entries.add(entity);
            }


        } catch (SQLException e) {
            log.error("Error while searching for table {}", tableName, e);
        }

        return entries;
    }

    private PreparedStatement prepareQueryStatement(Person author, Team team, OffsetDateTime start, OffsetDateTime end, Connection connection, String query) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setLong(1, author.getId());
        statement.setLong(2, team.getId());
        statement.setObject(3, start);
        statement.setObject(4, end);
        return statement;
    }

}
