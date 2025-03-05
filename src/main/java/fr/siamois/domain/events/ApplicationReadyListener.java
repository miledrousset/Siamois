package fr.siamois.domain.events;

import fr.siamois.infrastructure.database.AdminInitializer;
import fr.siamois.infrastructure.database.HistoryTriggerInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.sql.SQLException;

@Slf4j
@Configuration
public class ApplicationReadyListener {

    private final HistoryTriggerInitializer historyTriggerInitializer;
    private final AdminInitializer adminInitializer;

    public ApplicationReadyListener(HistoryTriggerInitializer historyTriggerInitializer,
                                    AdminInitializer adminInitializer) {
        this.historyTriggerInitializer = historyTriggerInitializer;
        this.adminInitializer = adminInitializer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (adminInitializer.initializeAdmin())
            log.info("ADMIN user created");

        if (adminInitializer.initializeAdminOrganization())
            log.info("Siamois Administration created");

        try {
            historyTriggerInitializer.createHistoryTriggers();
            log.info("History trigger created");
        } catch (SQLException e) {
            log.error("Failed to create History Triggers", e);
        }
    }

}
