package fr.siamois.domain.models.exceptions.auth;

public class EmailAlreadyExistException extends InvalidEmailException{
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
