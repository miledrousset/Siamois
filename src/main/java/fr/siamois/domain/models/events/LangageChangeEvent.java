package fr.siamois.domain.models.events;

import org.springframework.context.ApplicationEvent;

public class LangageChangeEvent extends ApplicationEvent {
    public LangageChangeEvent(Object source) {
        super(source);
    }
}
