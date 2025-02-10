package fr.siamois.infrastructure.repositories.history;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.models.Institution;
import fr.siamois.models.TraceableEntity;
import fr.siamois.models.UserInfo;
import fr.siamois.models.auth.Person;
import fr.siamois.models.history.GlobalHistoryEntry;
import fr.siamois.models.history.HistoryUpdateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    public void setUp() {
        globalHistoryRepository = new GlobalHistoryInstitRepository(hikariDataSource);
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
}