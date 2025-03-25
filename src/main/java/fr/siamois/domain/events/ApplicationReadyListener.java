package fr.siamois.domain.events;

import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import fr.siamois.infrastructure.database.initializer.DatabaseInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.List;

@Slf4j
@Configuration
public class ApplicationReadyListener {

    private final List<DatabaseInitializer> databaseInitializer;

    public ApplicationReadyListener(List<DatabaseInitializer> initializers) {
        this.databaseInitializer = initializers;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        for (DatabaseInitializer initializer : databaseInitializer) {
            try {
                initializer.initialize();
            } catch (DatabaseDataInitException e) {
                log.error(e.getMessage(), e);
                System.exit(1);
            }
        }
    }

}
