package fr.siamois.config.database;

import fr.siamois.infrastructure.database.HistoryTriggerInitializer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Initialise all the history triggers when application is ready
 *
 * @author Julien Linget
 */
@Component
public class HistoryTriggerConfig {

    private final HistoryTriggerInitializer historyTriggerInitializer;

    public HistoryTriggerConfig(HistoryTriggerInitializer historyTriggerInitializer) {
        this.historyTriggerInitializer = historyTriggerInitializer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setupHistoryTriggers() {
        historyTriggerInitializer.createHistoryTriggers();
    }

}
