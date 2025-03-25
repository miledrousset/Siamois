package fr.siamois.infrastructure.database.initializer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class JoinTablesInitializerDiffblueTest {
    /**
     * Test {@link JoinTablesInitializer#initialize()}.
     * <ul>
     *   <li>Given {@link ResultSet} {@link ResultSet#getBoolean(int)} return {@code false}.</li>
     *   <li>Then calls {@link Statement#execute(String)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JoinTablesInitializer#initialize()}
     */
    @Test
    @DisplayName("Test initialize(); given ResultSet getBoolean(int) return 'false'; then calls execute(String)")
    @Tag("MaintainedByDiffblue")
    void testInitialize_givenResultSetGetBooleanReturnFalse_thenCallsExecute()
            throws DatabaseDataInitException, SQLException {
        // Arrange
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getBoolean(Mockito.anyInt())).thenReturn(false);
        doNothing().when(resultSet).close();
        Statement statement = mock(Statement.class);
        when(statement.execute(anyString())).thenReturn(true);
        when(statement.getUpdateCount()).thenReturn(3);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        doNothing().when(statement).close();
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        doNothing().when(connection).close();
        HikariDataSource hikariDataSource = mock(HikariDataSource.class);
        when(hikariDataSource.getConnection()).thenReturn(connection);

        // Act
        (new JoinTablesInitializer(hikariDataSource)).initialize();

        // Assert
        verify(hikariDataSource).getConnection();
        verify(connection).close();
        verify(connection, atLeast(1)).createStatement();
        verify(resultSet).close();
        verify(resultSet).getBoolean(1);
        verify(resultSet).next();
        verify(statement, atLeast(1)).close();
        verify(statement, atLeast(1)).execute(anyString());
        verify(statement).executeQuery("SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'spatial_hierarchy')");
        verify(statement, atLeast(1)).getUpdateCount();
    }

    /**
     * Test {@link JoinTablesInitializer#initialize()}.
     * <ul>
     *   <li>Given {@link ResultSet} {@link ResultSet#getBoolean(int)} return {@code true}.</li>
     *   <li>Then calls {@link ResultSet#getBoolean(int)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JoinTablesInitializer#initialize()}
     */
    @Test
    @DisplayName("Test initialize(); given ResultSet getBoolean(int) return 'true'; then calls getBoolean(int)")
    @Tag("MaintainedByDiffblue")
    void testInitialize_givenResultSetGetBooleanReturnTrue_thenCallsGetBoolean()
            throws DatabaseDataInitException, SQLException {
        // Arrange
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getBoolean(anyInt())).thenReturn(true);
        doNothing().when(resultSet).close();
        Statement statement = mock(Statement.class);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        doNothing().when(statement).close();
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        doNothing().when(connection).close();
        HikariDataSource hikariDataSource = mock(HikariDataSource.class);
        when(hikariDataSource.getConnection()).thenReturn(connection);

        // Act
        (new JoinTablesInitializer(hikariDataSource)).initialize();

        // Assert
        verify(hikariDataSource).getConnection();
        verify(connection).close();
        verify(connection).createStatement();
        verify(resultSet).close();
        verify(resultSet).getBoolean(1);
        verify(resultSet).next();
        verify(statement).close();
        verify(statement).executeQuery("SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'spatial_hierarchy')");
    }

    /**
     * Test {@link JoinTablesInitializer#initialize()}.
     * <ul>
     *   <li>Then throw {@link DatabaseDataInitException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link JoinTablesInitializer#initialize()}
     */
    @Test
    @DisplayName("Test initialize(); then throw DatabaseDataInitException")
    @Tag("MaintainedByDiffblue")
    void testInitialize_thenThrowDatabaseDataInitException() throws SQLException {
        // Arrange
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenThrow(new SQLException());
        doThrow(new SQLException()).when(resultSet).close();
        Statement statement = mock(Statement.class);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        doNothing().when(statement).close();
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        doNothing().when(connection).close();
        HikariDataSource hikariDataSource = mock(HikariDataSource.class);
        when(hikariDataSource.getConnection()).thenReturn(connection);

        JoinTablesInitializer initializer = new JoinTablesInitializer(hikariDataSource);

        // Act and Assert
        assertThrows(DatabaseDataInitException.class, initializer::initialize);
        verify(hikariDataSource).getConnection();
        verify(connection).close();
        verify(connection).createStatement();
        verify(resultSet).close();
        verify(resultSet).next();
        verify(statement).close();
        verify(statement).executeQuery("SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'spatial_hierarchy')");
    }
}
