package fr.siamois.domain.models.exceptions.auth;

public class InvalidUserInformationException extends Exception {
    public InvalidUserInformationException(String message) {
        super(message);
    }

}
