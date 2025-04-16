package fr.siamois.ui.email;

public interface EmailManager {
    void sendEmail(String to, String subject, String body);
}
