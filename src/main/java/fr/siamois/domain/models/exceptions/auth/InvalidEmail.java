package fr.siamois.domain.models.exceptions.auth;

public class InvalidEmail extends Exception {
    public InvalidEmail(String message) {
        super(message);
    }
}
