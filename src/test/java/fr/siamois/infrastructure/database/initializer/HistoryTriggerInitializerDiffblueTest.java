package fr.siamois.infrastructure.database.initializer;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import fr.siamois.domain.models.exceptions.database.WrongTableNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class HistoryTriggerInitializerDiffblueTest {
    /**
     * Test {@link HistoryTriggerInitializer#initialize()}.
     * <ul>
     *   <li>Given {@link ResultSet} {@link ResultSet#getString(String)} return {@code String}.</li>
     *   <li>Then calls {@link Connection#createStatement()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryTriggerInitializer#initialize()}
     */
    @Test
    @DisplayName("Test initialize(); given ResultSet getString(String) return 'String'; then calls createStatement()")
    @Tag("MaintainedByDiffblue")
    void testInitialize_givenResultSetGetStringReturnString_thenCallsCreateStatement()
            throws SQLException {
        // Arrange
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(Mockito.any())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(Mockito.anyInt(), anyString());
        doNothing().when(preparedStatement).close();
        Statement statement = mock(Statement.class);
        when(statement.execute(anyString())).thenReturn(true);
        doNothing().when(statement).close();
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        doNothing().when(connection).close();
        HikariDataSource dataSource = mock(HikariDataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);

        HistoryTriggerInitializer initializer = new HistoryTriggerInitializer(dataSource);

        // Act and Assert
        assertThrows(DatabaseDataInitException.class, initializer::initialize);
        verify(dataSource).getConnection();
        verify(connection).close();
        verify(connection, atLeast(1)).createStatement();
        verify(connection, atLeast(1))
                .prepareStatement("SELECT column_name FROM information_schema.columns WHERE table_name = ?");
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(eq(1), anyString());
        verify(resultSet, atLeast(1)).close();
        verify(resultSet, atLeast(1)).getString("column_name");
        verify(resultSet, atLeast(1)).next();
        verify(preparedStatement, atLeast(1)).close();
        verify(statement, atLeast(1)).close();
        verify(statement, atLeast(1)).execute(anyString());
    }

    /**
     * Test {@link HistoryTriggerInitializer#initialize()}.
     * <ul>
     *   <li>Given {@link ResultSet} {@link ResultSet#getString(String)} throw {@link SQLException#SQLException()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryTriggerInitializer#initialize()}
     */
    @Test
    @DisplayName("Test initialize(); given ResultSet getString(String) throw SQLException()")
    @Tag("MaintainedByDiffblue")
    void testInitialize_givenResultSetGetStringThrowSQLException() throws SQLException {
        // Arrange
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(anyString())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(Mockito.anyInt(), anyString());
        doNothing().when(preparedStatement).close();
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        doNothing().when(connection).close();
        HikariDataSource dataSource = mock(HikariDataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);

        HistoryTriggerInitializer initializer = new HistoryTriggerInitializer(dataSource);

        // Act and Assert
        assertThrows(DatabaseDataInitException.class, initializer::initialize);
        verify(dataSource).getConnection();
        verify(connection).close();
        verify(connection).prepareStatement("SELECT column_name FROM information_schema.columns WHERE table_name = ?");
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(1,"action_unit");
        verify(resultSet).close();
        verify(resultSet).getString("column_name");
        verify(resultSet).next();
        verify(preparedStatement).close();
    }

    /**
     * Test {@link HistoryTriggerInitializer#createSQLHistTrigger(Connection, String, String)}.
     * <ul>
     *   <li>Then calls {@link Connection#createStatement()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryTriggerInitializer#createSQLHistTrigger(Connection, String, String)}
     */
    @Test
    @DisplayName("Test createSQLHistTrigger(Connection, String, String); then calls createStatement()")
    @Tag("MaintainedByDiffblue")
    void testCreateSQLHistTrigger_thenCallsCreateStatement() throws SQLException {
        // Arrange
        HikariDataSource dataSource = mock(HikariDataSource.class);
        HistoryTriggerInitializer historyTriggerInitializer = new HistoryTriggerInitializer(dataSource);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(anyString())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(Mockito.anyInt(), anyString());
        doNothing().when(preparedStatement).close();
        Statement statement = mock(Statement.class);
        when(statement.execute(anyString())).thenReturn(true);
        doNothing().when(statement).close();
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Act
        historyTriggerInitializer.createSQLHistTrigger(connection, "Table Name", "History Table Name");

        // Assert
        verify(connection, atLeast(1)).createStatement();
        verify(connection).prepareStatement("SELECT column_name FROM information_schema.columns WHERE table_name = ?");
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(1, "Table Name");
        verify(resultSet).close();
        verify(resultSet, atLeast(1)).getString("column_name");
        verify(resultSet, atLeast(1)).next();
        verify(preparedStatement).close();
        verify(statement, atLeast(1)).close();
        verify(statement, atLeast(1)).execute(anyString());
    }

    /**
     * Test {@link HistoryTriggerInitializer#createSQLHistTrigger(Connection, String, String)}.
     * <ul>
     *   <li>Then throw {@link SQLException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryTriggerInitializer#createSQLHistTrigger(Connection, String, String)}
     */
    @Test
    @DisplayName("Test createSQLHistTrigger(Connection, String, String); then throw SQLException")
    @Tag("MaintainedByDiffblue")
    void testCreateSQLHistTrigger_thenThrowSQLException() throws SQLException {
        // Arrange
        HikariDataSource dataSource = mock(HikariDataSource.class);
        HistoryTriggerInitializer historyTriggerInitializer = new HistoryTriggerInitializer(dataSource);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(anyString())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(Mockito.anyInt(), anyString());
        doNothing().when(preparedStatement).close();
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Act and Assert
        assertThrows(SQLException.class,
                () -> historyTriggerInitializer.createSQLHistTrigger(connection, "Table Name", "History Table Name"));
        verify(connection).prepareStatement("SELECT column_name FROM information_schema.columns WHERE table_name = ?");
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(1, "Table Name");
        verify(resultSet).close();
        verify(resultSet).getString("column_name");
        verify(resultSet).next();
        verify(preparedStatement).close();
    }

    /**
     * Test {@link HistoryTriggerInitializer#createSQLHistTrigger(Connection, String, String)}.
     * <ul>
     *   <li>Then throw {@link WrongTableNameException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryTriggerInitializer#createSQLHistTrigger(Connection, String, String)}
     */
    @Test
    @DisplayName("Test createSQLHistTrigger(Connection, String, String); then throw WrongTableNameException")
    @Tag("MaintainedByDiffblue")
    void testCreateSQLHistTrigger_thenThrowWrongTableNameException() throws SQLException {
        // Arrange
        HikariDataSource dataSource = mock(HikariDataSource.class);
        HistoryTriggerInitializer historyTriggerInitializer = new HistoryTriggerInitializer(dataSource);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(Mockito.anyInt(), anyString());
        doNothing().when(preparedStatement).close();
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        // Act and Assert
        assertThrows(WrongTableNameException.class,
                () -> historyTriggerInitializer.createSQLHistTrigger(connection, "Table Name", "History Table Name"));
        verify(connection).prepareStatement("SELECT column_name FROM information_schema.columns WHERE table_name = ?");
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(1, "Table Name");
        verify(resultSet).close();
        verify(resultSet).next();
        verify(preparedStatement).close();
    }

    /**
     * Test {@link HistoryTriggerInitializer#createHistoryTrigger(Connection, String)}.
     * <ul>
     *   <li>Given {@link Statement} {@link Statement#execute(String)} return {@code true}.</li>
     *   <li>Then calls {@link Statement#close()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryTriggerInitializer#createHistoryTrigger(Connection, String)}
     */
    @Test
    @DisplayName("Test createHistoryTrigger(Connection, String); given Statement execute(String) return 'true'; then calls close()")
    @Tag("MaintainedByDiffblue")
    void testCreateHistoryTrigger_givenStatementExecuteReturnTrue_thenCallsClose() throws SQLException {
        // Arrange
        HikariDataSource dataSource = mock(HikariDataSource.class);
        HistoryTriggerInitializer historyTriggerInitializer = new HistoryTriggerInitializer(dataSource);
        Statement statement = mock(Statement.class);
        when(statement.execute(anyString())).thenReturn(true);
        doNothing().when(statement).close();
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);

        // Act
        historyTriggerInitializer.createHistoryTrigger(connection, "Table Name");

        // Assert
        verify(connection).createStatement();
        verify(statement).close();
        verify(statement).execute("CREATE OR REPLACE TRIGGER trg_save_history_Table Name\nAFTER UPDATE OR DELETE ON Table Name\nFOR EACH ROW EXECUTE FUNCTION save_history_Table Name();\n");
    }

    /**
     * Test {@link HistoryTriggerInitializer#createHistoryTrigger(Connection, String)}.
     * <ul>
     *   <li>Then throw {@link WrongTableNameException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryTriggerInitializer#createHistoryTrigger(Connection, String)}
     */
    @Test
    @DisplayName("Test createHistoryTrigger(Connection, String); then throw WrongTableNameException")
    @Tag("MaintainedByDiffblue")
    void testCreateHistoryTrigger_thenThrowWrongTableNameException() throws SQLException {
        // Arrange
        HikariDataSource dataSource = mock(HikariDataSource.class);
        HistoryTriggerInitializer historyTriggerInitializer = new HistoryTriggerInitializer(dataSource);
        Statement statement = mock(Statement.class);
        when(statement.execute(anyString())).thenThrow(new WrongTableNameException("An error occurred"));
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);

        // Act and Assert
        assertThrows(WrongTableNameException.class,
                () -> historyTriggerInitializer.createHistoryTrigger(connection, "Table Name"));
        verify(connection).createStatement();
        verify(statement).execute("CREATE OR REPLACE TRIGGER trg_save_history_Table Name\nAFTER UPDATE OR DELETE ON Table Name\nFOR EACH ROW EXECUTE FUNCTION save_history_Table Name();\n");
    }

    /**
     * Test {@link HistoryTriggerInitializer#createHistoryFunction(Connection, String, String, StringBuilder, StringBuilder)}.
     * <ul>
     *   <li>Given {@link Statement} {@link Statement#execute(String)} return {@code true}.</li>
     *   <li>Then calls {@link Statement#close()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryTriggerInitializer#createHistoryFunction(Connection, String, String, StringBuilder, StringBuilder)}
     */
    @Test
    @DisplayName("Test createHistoryFunction(Connection, String, String, StringBuilder, StringBuilder); given Statement execute(String) return 'true'; then calls close()")
    @Tag("MaintainedByDiffblue")
    void testCreateHistoryFunction_givenStatementExecuteReturnTrue_thenCallsClose() throws SQLException {
        // Arrange
        HikariDataSource dataSource = mock(HikariDataSource.class);
        HistoryTriggerInitializer historyTriggerInitializer = new HistoryTriggerInitializer(dataSource);
        Statement statement = mock(Statement.class);
        when(statement.execute(anyString())).thenReturn(true);
        doNothing().when(statement).close();
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        StringBuilder columnList = new StringBuilder("foo");

        // Act
        historyTriggerInitializer.createHistoryFunction(connection, "Table Name", "History Table Name", columnList,
                new StringBuilder("foo"));

        // Assert
        verify(connection).createStatement();
        verify(statement).close();
        verify(statement).execute("CREATE OR REPLACE FUNCTION save_history_Table Name() RETURNS TRIGGER AS \n$$\nBEGIN\n\tINSERT INTO History Table Name(foo, update_type, update_time)\n\tSELECT foo, TG_OP, NOW();\n\tRETURN NEW;\nEND\n$$\nLANGUAGE plpgsql;\n");
    }

    /**
     * Test {@link HistoryTriggerInitializer#createHistoryFunction(Connection, String, String, StringBuilder, StringBuilder)}.
     * <ul>
     *   <li>Then throw {@link WrongTableNameException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryTriggerInitializer#createHistoryFunction(Connection, String, String, StringBuilder, StringBuilder)}
     */
    @Test
    @DisplayName("Test createHistoryFunction(Connection, String, String, StringBuilder, StringBuilder); then throw WrongTableNameException")
    @Tag("MaintainedByDiffblue")
    void testCreateHistoryFunction_thenThrowWrongTableNameException() throws SQLException {
        // Arrange
        HikariDataSource dataSource = mock(HikariDataSource.class);
        HistoryTriggerInitializer historyTriggerInitializer = new HistoryTriggerInitializer(dataSource);
        Statement statement = mock(Statement.class);
        when(statement.execute(anyString())).thenThrow(new WrongTableNameException("An error occurred"));
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        StringBuilder columnList = new StringBuilder("foo");

        // Act and Assert
        assertThrows(WrongTableNameException.class, () -> historyTriggerInitializer.createHistoryFunction(connection,
                "Table Name", "History Table Name", columnList, new StringBuilder("foo")));
        verify(connection).createStatement();
        verify(statement).execute("CREATE OR REPLACE FUNCTION save_history_Table Name() RETURNS TRIGGER AS \n$$\nBEGIN\n\tINSERT INTO History Table Name(foo, update_type, update_time)\n\tSELECT foo, TG_OP, NOW();\n\tRETURN NEW;\nEND\n$$\nLANGUAGE plpgsql;\n");
    }

    /**
     * Test {@link HistoryTriggerInitializer#addColumnNamesToLists(Connection, String, StringBuilder, StringBuilder)}.
     * <ul>
     *   <li>Then {@link StringBuilder#StringBuilder(String)} with {@code foo} toString is {@code fooString, String}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryTriggerInitializer#addColumnNamesToLists(Connection, String, StringBuilder, StringBuilder)}
     */
    @Test
    @DisplayName("Test addColumnNamesToLists(Connection, String, StringBuilder, StringBuilder); then StringBuilder(String) with 'foo' toString is 'fooString, String'")
    @Tag("MaintainedByDiffblue")
    void testAddColumnNamesToLists_thenStringBuilderWithFooToStringIsFooStringString() throws SQLException {
        // Arrange
        HikariDataSource dataSource = mock(HikariDataSource.class);
        HistoryTriggerInitializer historyTriggerInitializer = new HistoryTriggerInitializer(dataSource);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(anyString())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), anyString());
        doNothing().when(preparedStatement).close();
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        StringBuilder columnList = new StringBuilder("foo");

        // Act
        historyTriggerInitializer.addColumnNamesToLists(connection, "Table Name", columnList, new StringBuilder("foo"));

        // Assert
        verify(connection).prepareStatement("SELECT column_name FROM information_schema.columns WHERE table_name = ?");
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(1, "Table Name");
        verify(resultSet).close();
        verify(resultSet, atLeast(1)).getString("column_name");
        verify(resultSet, atLeast(1)).next();
        verify(preparedStatement).close();
        assertEquals("fooString, String", columnList.toString());
    }

    /**
     * Test {@link HistoryTriggerInitializer#addColumnNamesToLists(Connection, String, StringBuilder, StringBuilder)}.
     * <ul>
     *   <li>Then throw {@link SQLException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryTriggerInitializer#addColumnNamesToLists(Connection, String, StringBuilder, StringBuilder)}
     */
    @Test
    @DisplayName("Test addColumnNamesToLists(Connection, String, StringBuilder, StringBuilder); then throw SQLException")
    @Tag("MaintainedByDiffblue")
    void testAddColumnNamesToLists_thenThrowSQLException() throws SQLException {
        // Arrange
        HikariDataSource dataSource = mock(HikariDataSource.class);
        HistoryTriggerInitializer historyTriggerInitializer = new HistoryTriggerInitializer(dataSource);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(anyString())).thenThrow(new SQLException());
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(Mockito.anyInt(), anyString());
        doNothing().when(preparedStatement).close();
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        StringBuilder columnList = new StringBuilder("foo");

        // Act and Assert
        assertThrows(SQLException.class, () -> historyTriggerInitializer.addColumnNamesToLists(connection, "Table Name",
                columnList, new StringBuilder("foo")));
        verify(connection).prepareStatement("SELECT column_name FROM information_schema.columns WHERE table_name = ?");
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setString(1, "Table Name");
        verify(resultSet).close();
        verify(resultSet).getString("column_name");
        verify(resultSet).next();
        verify(preparedStatement).close();
    }
}
