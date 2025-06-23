package fr.siamois.domain.events.publisher;

import fr.siamois.domain.models.events.LangageChangeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class LangageChangeEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public LangageChangeEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishInstitutionChangeEvent() {
        LangageChangeEvent event = new LangageChangeEvent(this);
        applicationEventPublisher.publishEvent(event);
    }

}
