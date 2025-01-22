package fr.siamois.services.publisher;

import fr.siamois.models.events.TeamChangeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class TeamChangeEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public TeamChangeEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishTeamChangeEvent() {
        TeamChangeEvent event = new TeamChangeEvent(this);
        applicationEventPublisher.publishEvent(event);
    }

}
