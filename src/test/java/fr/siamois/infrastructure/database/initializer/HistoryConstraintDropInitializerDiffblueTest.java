package fr.siamois.infrastructure.database.initializer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class HistoryConstraintDropInitializerDiffblueTest {
    /**
     * Test {@link HistoryConstraintDropInitializer#initialize()}.
     * <ul>
     *   <li>Given {@code false}.</li>
     *   <li>Then calls {@link Statement#executeBatch()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link HistoryConstraintDropInitializer#initialize()}
     */
    @Test
    @DisplayName("Test initialize(); given 'false'; then calls executeBatch()")
    @Tag("MaintainedByDiffblue")
    void testInitialize_givenFalse_thenCallsExecuteBatch() throws DatabaseDataInitException, SQLException {
        // Arrange
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString(anyInt())).thenReturn("String");
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        doNothing().when(resultSet).close();
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        doNothing().when(preparedStatement).setString(anyInt(), anyString());
        doNothing().when(preparedStatement).close();
        Statement statement = mock(Statement.class);
        when(statement.executeBatch()).thenReturn(new int[]{1, -1, 1, -1});
        doNothing().when(statement).addBatch(anyString());
        doNothing().when(statement).close();
        Connection connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.createStatement()).thenReturn(statement);
        doNothing().when(connection).close();
        HikariDataSource hikariDataSource = mock(HikariDataSource.class);
        when(hikariDataSource.getConnection()).thenReturn(connection);

        // Act
        (new HistoryConstraintDropInitializer(hikariDataSource)).initialize();

        // Assert
        verify(hikariDataSource).getConnection();
        verify(connection).close();
        verify(connection).createStatement();
        verify(connection, atLeast(1)).prepareStatement("SELECT con.conname\nFROM pg_catalog.pg_constraint con\n         INNER JOIN pg_catalog.pg_class rel ON rel.oid = con.conrelid\n         INNER JOIN pg_catalog.pg_namespace nsp ON nsp.oid = connamespace\nWHERE nsp.nspname = 'public'\n  AND rel.relname = ?\n  AND con.contype != 'p'\n");
        verify(preparedStatement, atLeast(1)).executeQuery();
        verify(preparedStatement, atLeast(1)).setString(eq(1), anyString());
        verify(resultSet, atLeast(1)).close();
        verify(resultSet, atLeast(1)).getString(1);
        verify(resultSet, atLeast(1)).next();
        verify(statement, atLeast(1)).addBatch(anyString());
        verify(statement).close();
        verify(preparedStatement, atLeast(1)).close();
        verify(statement).executeBatch();
    }
}
