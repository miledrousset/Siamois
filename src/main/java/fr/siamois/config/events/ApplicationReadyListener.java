package fr.siamois.config.events;

import fr.siamois.infrastructure.database.HistoryTriggerInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
public class ApplicationReadyListener {

    private final HistoryTriggerInitializer historyTriggerInitializer;

    public ApplicationReadyListener(HistoryTriggerInitializer historyTriggerInitializer) {
        this.historyTriggerInitializer = historyTriggerInitializer;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        historyTriggerInitializer.createHistoryTriggers();
        log.info("History trigger created");
    }

}
