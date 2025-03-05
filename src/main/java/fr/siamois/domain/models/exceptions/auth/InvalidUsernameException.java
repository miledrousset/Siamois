package fr.siamois.domain.models.exceptions.auth;

public class InvalidUsernameException extends InvalidUserInformationException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
