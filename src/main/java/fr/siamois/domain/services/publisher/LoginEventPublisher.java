package fr.siamois.domain.services.publisher;

import fr.siamois.domain.models.events.LoginEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class LoginEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public LoginEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishLoginEvent() {
        LoginEvent event = new LoginEvent(this);
        applicationEventPublisher.publishEvent(event);
    }

}
