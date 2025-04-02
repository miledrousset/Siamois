package fr.siamois.infrastructure.database.initializer;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class StratiTriggerInitializerDiffblueTest {
    /**
     * Test {@link StratiTriggerInitializer#initialize()}.
     * <ul>
     *   <li>Then calls {@link Statement#executeBatch()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StratiTriggerInitializer#initialize()}
     */
    @Test
    @DisplayName("Test initialize(); then calls executeBatch()")
    @Tag("MaintainedByDiffblue")
    void testInitialize_thenCallsExecuteBatch() throws DatabaseDataInitException, SQLException {
        // Arrange
        Statement statement = mock(Statement.class);
        when(statement.executeBatch()).thenReturn(new int[]{1, -1, 1, -1});
        doNothing().when(statement).addBatch(anyString());
        doNothing().when(statement).close();
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        doNothing().when(connection).close();
        HikariDataSource hikariDataSource = mock(HikariDataSource.class);
        when(hikariDataSource.getConnection()).thenReturn(connection);

        // Act
        (new StratiTriggerInitializer(hikariDataSource)).initialize();

        // Assert
        verify(hikariDataSource).getConnection();
        verify(connection).close();
        verify(connection).createStatement();
        verify(statement, atLeast(1)).addBatch(anyString());
        verify(statement).close();
        verify(statement).executeBatch();
    }

    /**
     * Test {@link StratiTriggerInitializer#initialize()}.
     * <ul>
     *   <li>Then throw {@link DatabaseDataInitException}.</li>
     * </ul>
     * <p>
     * Method under test: {@link StratiTriggerInitializer#initialize()}
     */
    @Test
    @DisplayName("Test initialize(); then throw DatabaseDataInitException")
    @Tag("MaintainedByDiffblue")
    void testInitialize_thenThrowDatabaseDataInitException() throws SQLException {
        // Arrange
        Statement statement = mock(Statement.class);
        doThrow(new SQLException()).when(statement).addBatch(anyString());
        doThrow(new SQLException()).when(statement).close();
        Connection connection = mock(Connection.class);
        when(connection.createStatement()).thenReturn(statement);
        doNothing().when(connection).close();
        HikariDataSource hikariDataSource = mock(HikariDataSource.class);
        when(hikariDataSource.getConnection()).thenReturn(connection);

        StratiTriggerInitializer initializer = new StratiTriggerInitializer(hikariDataSource);

        // Act and Assert
        assertThrows(DatabaseDataInitException.class, initializer::initialize);
        verify(hikariDataSource).getConnection();
        verify(connection).close();
        verify(connection).createStatement();
        verify(statement).addBatch("CREATE OR REPLACE FUNCTION order_strat_relationship()\nRETURNS TRIGGER AS\n$$\nBEGIN\nIF EXISTS (SELECT 1\nFROM stratigraphic_relationship\nWHERE fk_recording_unit_1_id = NEW.fk_recording_unit_2_id\n  AND fk_recording_unit_2_id = NEW.fk_recording_unit_1_id) THEN\nRETURN NULL;\nEND IF;\nRETURN NEW;\nEND;\n$$ LANGUAGE plpgsql;\n");
        verify(statement).close();
    }
}
