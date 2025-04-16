package fr.siamois.domain.models.events;

import org.springframework.context.ApplicationEvent;

public class LoginEvent extends ApplicationEvent {
    public LoginEvent(Object source) {
        super(source);
    }
}
