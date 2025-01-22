package fr.siamois.models.events;

import org.springframework.context.ApplicationEvent;

public class TeamChangeEvent extends ApplicationEvent {
    public TeamChangeEvent(Object source) {
        super(source);
    }
}
