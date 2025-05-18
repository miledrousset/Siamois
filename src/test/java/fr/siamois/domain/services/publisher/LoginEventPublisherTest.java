package fr.siamois.domain.services.publisher;


import fr.siamois.domain.models.events.LoginEvent;
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
class LoginEventPublisherTest {

    @Mock
    private ApplicationEventPublisher publisher;

    private LoginEventPublisher loginEventPublisher;

    @BeforeEach
    void setUp() {
        loginEventPublisher = new LoginEventPublisher(publisher);
    }

    @Test
    void publishLoginEvent_shouldSendChangeEvent() {
        loginEventPublisher.publishLoginEvent();
        verify(publisher, times(1)).publishEvent(any(LoginEvent.class));
    }
}