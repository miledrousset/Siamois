package fr.siamois.domain.services.publisher;

import fr.siamois.domain.models.events.InstitutionChangeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class InstitutionChangeEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public InstitutionChangeEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishInstitutionChangeEvent() {
        InstitutionChangeEvent event = new InstitutionChangeEvent(this);
        applicationEventPublisher.publishEvent(event);
    }

}
