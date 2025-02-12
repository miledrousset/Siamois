package fr.siamois.infrastructure.database;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryTriggerInitializerTest {

    @Mock
    private HikariDataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private Statement statement;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    private HistoryTriggerInitializer historyTriggerInitializer;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true, false, true, false, true, false, true, false, true, false, true, false, true, false);
        when(resultSet.getString("column_name")).thenReturn("test_id");

        historyTriggerInitializer = new HistoryTriggerInitializer(dataSource);
    }

    @Test
    void createHistoryTriggers() throws SQLException {
        when(connection.createStatement()).thenReturn(statement);

        historyTriggerInitializer.createHistoryTriggers();

        verify(statement, times(14)).execute(anyString());
    }

    @Test
    void createHistoryTriggersThrowsSQLException() throws SQLException {
        doThrow(new SQLException("Error creating statement")).when(connection).createStatement();

        assertThrows(SQLException.class, () -> historyTriggerInitializer.createHistoryTriggers());
    }
}