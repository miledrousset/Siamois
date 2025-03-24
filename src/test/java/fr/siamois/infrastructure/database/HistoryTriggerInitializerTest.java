package fr.siamois.infrastructure.database;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.domain.models.exceptions.database.WrongTableNameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

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
    @Mock
    private ApplicationContext applicationContext;

    private HistoryTriggerInitializer historyTriggerInitializer;

    @BeforeEach
    void setUp() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("column_name")).thenReturn("test_id");

        historyTriggerInitializer = new HistoryTriggerInitializer(dataSource, applicationContext);
    }

    @Test
    void initializeShouldCreateHistoryTriggers() throws SQLException {
        when(connection.createStatement()).thenReturn(statement);

        historyTriggerInitializer.initialize();

        verify(statement, times(7)).execute(anyString());
    }

    @Test
    void initializeShouldThrowSQLException() throws SQLException {
        doThrow(new SQLException("Error creating statement")).when(connection).createStatement();

        assertThrows(SQLException.class, () -> historyTriggerInitializer.initialize());
    }

    @Test
    void createSQLHistTriggerShouldThrowSQLException() throws SQLException {
        doThrow(new SQLException("Error creating statement")).when(connection).createStatement();

        assertThrows(SQLException.class, () -> historyTriggerInitializer.createSQLHistTrigger(connection, "test_table", "history_test_table"));
    }

    @Test
    void addColumnNamesToListsShouldThrowWrongTableNameException() throws SQLException {
        when(resultSet.next()).thenReturn(false);

        assertThrows(WrongTableNameException.class, () -> historyTriggerInitializer.addColumnNamesToLists(connection, "non_existent_table", new StringBuilder(), new StringBuilder()));
    }
}