package fr.siamois.domain.services.publisher;

import fr.siamois.domain.models.events.InstitutionChangeEvent;
import fr.siamois.domain.services.publisher.InstitutionChangeEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InstitutionChangeEventPublisherTest {

    @Mock private ApplicationEventPublisher publisher;

    private InstitutionChangeEventPublisher institutionChangeEventPublisher;

    @BeforeEach
    void setUp() {
        institutionChangeEventPublisher = new InstitutionChangeEventPublisher(publisher);
    }

    @Test
    void publishInstitutionChangeEvent_shouldSendChangeEvent() {
        institutionChangeEventPublisher.publishInstitutionChangeEvent();
        verify(publisher, times(1)).publishEvent(any(InstitutionChangeEvent.class));
    }
}