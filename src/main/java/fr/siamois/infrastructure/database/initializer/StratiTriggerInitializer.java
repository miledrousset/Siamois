package fr.siamois.infrastructure.database.initializer;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@Service
@Order(4)
public class StratiTriggerInitializer implements DatabaseInitializer {

    private final HikariDataSource hikariDataSource;

    public StratiTriggerInitializer(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public void initialize() throws DatabaseDataInitException {
        try (Connection connection = hikariDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.addBatch(STRATI_TRIGGER);
            statement.addBatch("DROP TRIGGER IF EXISTS prevent_reverse_relationship ON stratigraphic_relationship;");
            statement.addBatch(CREATE_TRIGGER);
            statement.executeBatch();
            log.info("StratiTriggerInitializer initialized");
        } catch (SQLException e) {
            log.error("StratiTriggerInitializer failed", e);
            throw new DatabaseDataInitException("StratiTriggerInitializer failed", e);
        }
    }

    private static final String STRATI_TRIGGER =
            """
                    CREATE OR REPLACE FUNCTION order_strat_relationship()
                    RETURNS TRIGGER AS
                    $$
                    BEGIN
                    IF EXISTS (SELECT 1
                    FROM stratigraphic_relationship
                    WHERE fk_recording_unit_1_id = NEW.fk_recording_unit_2_id
                      AND fk_recording_unit_2_id = NEW.fk_recording_unit_1_id) THEN
                    RETURN NULL;
                    END IF;
                    RETURN NEW;
                    END;
                    $$ LANGUAGE plpgsql;
                    """;

    private static final String CREATE_TRIGGER =
            """
                    CREATE TRIGGER prevent_reverse_relationship
                        BEFORE INSERT ON stratigraphic_relationship
                        FOR EACH ROW
                    EXECUTE FUNCTION order_strat_relationship();
                    """;

}
