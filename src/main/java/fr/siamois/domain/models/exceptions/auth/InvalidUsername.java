package fr.siamois.domain.models.exceptions.auth;

public class InvalidUsername extends InvalidUserInformation {
    public InvalidUsername(String message) {
        super(message);
    }
}
