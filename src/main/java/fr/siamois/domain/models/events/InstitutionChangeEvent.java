package fr.siamois.domain.models.events;

import org.springframework.context.ApplicationEvent;

public class InstitutionChangeEvent extends ApplicationEvent {
    public InstitutionChangeEvent(Object source) {
        super(source);
    }
}
