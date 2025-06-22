package fr.siamois.infrastructure.database.initializer;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexGistInitializerTest {

    @Mock
    private HikariDataSource hikariDataSource;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @InjectMocks
    private IndexGistInitializer initializer;

    @Test
    void initialize_shouldCreateGistIndexesSuccessfully() throws Exception {
        // Given
        when(hikariDataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);

        // When
        initializer.initialize();

        // Then
        verify(statement).addBatch("CREATE INDEX IF NOT EXISTS users_username_trgm ON person USING gist (username gist_trgm_ops);");
        verify(statement).addBatch("CREATE INDEX IF NOT EXISTS users_email_trgm ON person USING gist (mail gist_trgm_ops);");
        verify(statement).executeBatch();

        verify(connection).close();
        verify(statement).close();
    }

    @Test
    void initialize_shouldThrowDatabaseDataInitException_whenSQLExceptionOccurs() throws Exception {
        // Given
        SQLException sqlException = new SQLException("Connection failed");
        when(hikariDataSource.getConnection()).thenThrow(sqlException);

        // When / Then
        DatabaseDataInitException thrown = assertThrows(
                DatabaseDataInitException.class,
                () -> initializer.initialize()
        );

        assertEquals("Error while creating GIST indexes", thrown.getMessage());
        assertEquals(sqlException, thrown.getCause());
    }
}