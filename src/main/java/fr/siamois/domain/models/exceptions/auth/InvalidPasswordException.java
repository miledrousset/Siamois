package fr.siamois.domain.models.exceptions.auth;

public class InvalidPasswordException extends InvalidUserInformationException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
