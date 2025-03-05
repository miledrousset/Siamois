package fr.siamois.domain.models.exceptions.auth;

public class InvalidNameException extends InvalidUserInformationException {
    public InvalidNameException(String message) {
        super(message);
    }
}
