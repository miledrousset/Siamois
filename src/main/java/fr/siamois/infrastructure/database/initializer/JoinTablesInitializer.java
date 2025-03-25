package fr.siamois.infrastructure.database.initializer;

import com.zaxxer.hikari.HikariDataSource;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Service
@Order(5)
public class JoinTablesInitializer implements DatabaseInitializer {

    private final HikariDataSource hikariDataSource;

    public JoinTablesInitializer(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public void initialize() throws DatabaseDataInitException {
        try (Connection connection = hikariDataSource.getConnection()) {
            // Check if the table action_action_code exists
            boolean tableExists = verifyTableExistance(connection);

            if (!tableExists) {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                Resource resource = new ClassPathResource("sql/join_tables.sql");
                populator.addScript(resource);
                populator.populate(connection);
            }
        } catch (SQLException e) {
            log.error("Initializing join tables failed", e);
            throw new DatabaseDataInitException("Initializing join tables failed", e);
        }
    }

    private static boolean verifyTableExistance(Connection connection) throws SQLException {
        boolean tableExists;
        try (var statement = connection.createStatement();
             var resultSet = statement.executeQuery("SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'spatial_hierarchy')")) {
            resultSet.next();
            tableExists = resultSet.getBoolean(1);
        }
        return tableExists;
    }
}
