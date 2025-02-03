package fr.siamois.infrastructure.repositories.history;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.models.TraceInfo;
import fr.siamois.models.TraceableEntity;
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
public class GlobalHistoryInstitRepository implements GlobalHistoryRepository {

    private final HikariDataSource hikariDataSource;

    private static final List<String> existingTableNames = new ArrayList<>();

    public GlobalHistoryInstitRepository(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    private void populateTablenameList() {
        if (!existingTableNames.isEmpty()) return;

        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                existingTableNames.add(resultSet.getString("table_name"));
            }
        } catch (SQLException e) {
            log.error("Error while populating table names", e);
            existingTableNames.clear();
        }
    }

    private boolean tableNameNotExist(String tablename) {
        return existingTableNames.stream()
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

    private void logNotExistTable(String tableName) {
        log.error("Table name {} does not exist", tableName);
    }

    private PreparedStatement prepareQueryStatement(TraceInfo traceInfo, OffsetDateTime start, OffsetDateTime end, Connection connection, String query) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setLong(1, traceInfo.author().getId());
        statement.setLong(2, traceInfo.institution().getId());
        statement.setObject(3, start);
        statement.setObject(4, end);
        return statement;
    }

    @Override
    public List<GlobalHistoryEntry> findAllHistoryOfUserBetween(String tableName, TraceInfo traceInfo, OffsetDateTime start, OffsetDateTime end) {
        List<GlobalHistoryEntry> entries = new ArrayList<>();
        populateTablenameList();

        if (tableNameNotExist(tableName)) {
            logNotExistTable(tableName);
            return entries;
        }

        try (Connection connection = hikariDataSource.getConnection()) {
            String query = "SELECT * FROM " + tableName +" WHERE fk_author_id = ? AND fk_institution_id = ? AND update_time BETWEEN ? AND ?";
            PreparedStatement statement = prepareQueryStatement(traceInfo, start, end, connection, query);

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

    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class LocalTraceableEntity extends TraceableEntity {
        private Long id;
    }

    @Override
    public List<TraceableEntity> findAllCreationOfUserBetween(String tableName, TraceInfo traceInfo, OffsetDateTime start, OffsetDateTime end) {
        List<TraceableEntity> entries = new ArrayList<>();
        populateTablenameList();

        if (tableNameNotExist(tableName)) {
            logNotExistTable(tableName);
            return entries;
        }

        try (Connection connection = hikariDataSource.getConnection()) {
            String query = "SELECT * FROM " + tableName +" WHERE fk_author_id = ? AND fk_institution_id = ? AND update_time BETWEEN ? AND ?";
            PreparedStatement statement = prepareQueryStatement(traceInfo, start, end, connection, query);

            ResultSet resultSet = statement.executeQuery();
            String idColumnName = findColumnTableIdNameInResultSet(resultSet);

            while (resultSet.next()) {
                OffsetDateTime creationTime = resultSet.getObject("creation_time", OffsetDateTime.class);
                Long tableId = resultSet.getLong(idColumnName);

                GlobalHistoryInstitRepository.LocalTraceableEntity entity = new GlobalHistoryInstitRepository.LocalTraceableEntity();
                entity.setAuthor(traceInfo.author());
                entity.setCreationTime(creationTime);
                entity.setId(tableId);

                entries.add(entity);
            }


        } catch (SQLException e) {
            log.error("Error while searching for table {}", tableName, e);
        }

        return entries;
    }
}
