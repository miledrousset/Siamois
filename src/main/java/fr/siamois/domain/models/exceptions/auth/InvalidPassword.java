package fr.siamois.domain.models.exceptions.auth;

public class InvalidPassword extends Exception {
    public InvalidPassword(String message) {
        super(message);
    }
}
