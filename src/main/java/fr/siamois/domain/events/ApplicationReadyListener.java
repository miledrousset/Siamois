package fr.siamois.domain.events;

import fr.siamois.infrastructure.database.DatabaseInitializer;
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
            initializer.initialize();
        }
    }

}
