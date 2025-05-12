package fr.siamois.infrastructure.database.repositories.history;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.history.GlobalHistoryEntry;
import fr.siamois.domain.models.history.HistoryUpdateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalHistoryInstitRepositoryTest {

    @Mock private HikariDataSource hikariDataSource;
    @Mock private Connection connection;
    @Mock private PreparedStatement preparedStatement;
    @Mock private ResultSet resultSet;
    @Mock private ResultSetMetaData resultSetMetaData;
    private UserInfo userInfo;

    private GlobalHistoryRepository globalHistoryRepository;

    @BeforeEach
    void setUp() {
        globalHistoryRepository = new GlobalHistoryInstitRepository(hikariDataSource);
        GlobalHistoryInstitRepository.existingTableNames.clear();
    }

    private void setupUserInfo() {
        userInfo = new UserInfo(new Institution(), new Person(), "fr");
        userInfo.getInstitution().setId(-1L);
        userInfo.getInstitution().setName("Name");
        userInfo.getUser().setId(-1L);
        userInfo.getUser().setUsername("Username");
    }

    @Test
    void findAllHistoryOfUserBetween() throws SQLException {
        when(hikariDataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);

        when(resultSetMetaData.getColumnCount()).thenReturn(1);
        when(resultSetMetaData.getColumnName(1)).thenReturn("test_id");

        when(resultSet.next()).thenReturn(true, false, true, false);
        when(resultSet.getString("table_name")).thenReturn("test_table");

        setupUserInfo();

        when(resultSet.getString("update_type")).thenReturn("UPDATE");
        when(resultSet.getObject("update_time", OffsetDateTime.class)).thenReturn(OffsetDateTime.now());
        when(resultSet.getLong(anyString())).thenReturn(1L);

        List<GlobalHistoryEntry> entries = globalHistoryRepository.findAllHistoryOfUserBetween("test_table", userInfo, OffsetDateTime.now().minusDays(1), OffsetDateTime.now());

        assertEquals(1, entries.size());
        assertEquals(HistoryUpdateType.UPDATE, entries.get(0).getUpdateType());
    }

    @Test
    void findAllHistoryOfUserBetweenThrowsSQLException() throws SQLException {
        when(hikariDataSource.getConnection()).thenThrow(new SQLException("Connection error"));

        assertThrows(SQLException.class, () ->
                globalHistoryRepository.findAllHistoryOfUserBetween("test_table", userInfo, OffsetDateTime.now().minusDays(1), OffsetDateTime.now()));
    }

    @Test
    void findAllCreationOfUserBetween() throws SQLException {

        GlobalHistoryInstitRepository.existingTableNames.add("test_table");

        when(hikariDataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);

        when(resultSet.next()).thenReturn(true, false);

        setupUserInfo();

        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getObject("creation_time", OffsetDateTime.class)).thenReturn(OffsetDateTime.now());
        when(resultSet.getLong(anyString())).thenReturn(1L);

        List<TraceableEntity> entries = globalHistoryRepository.findAllCreationOfUserBetween("test_table", userInfo, OffsetDateTime.now().minusDays(1), OffsetDateTime.now());

        assertEquals(1, entries.size());
        assertEquals(1L, entries.get(0).getId());
    }

    @Test
    void findAllCreationOfUserBetweenThrowsSQLException() throws SQLException {
        when(hikariDataSource.getConnection()).thenThrow(new SQLException("Connection error"));

        assertThrows(SQLException.class,
                () -> globalHistoryRepository.findAllCreationOfUserBetween("test_table", userInfo, OffsetDateTime.now().minusDays(1), OffsetDateTime.now()));
    }

    @Test
    void populateTablenameListShouldThrowSQLException() throws SQLException {
        when(hikariDataSource.getConnection()).thenThrow(new SQLException("Connection error"));

        GlobalHistoryInstitRepository repository = new GlobalHistoryInstitRepository(hikariDataSource);

        assertThrows(SQLException.class, repository::populateTablenameList);
    }

}