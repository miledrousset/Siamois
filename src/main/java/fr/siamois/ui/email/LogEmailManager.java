package fr.siamois.ui.email;

import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogEmailManager implements EmailManager {

    @Override
    public void sendEmail(@Email String to, String subject, String body) {
        log.info("Email logged :\nSent to : \"{}\"\nSubject : \"{}\"\nBody :\n {}", to, subject, body);
    }

}
