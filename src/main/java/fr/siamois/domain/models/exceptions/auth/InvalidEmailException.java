package fr.siamois.domain.models.exceptions.auth;

public class InvalidEmailException extends InvalidUserInformationException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
