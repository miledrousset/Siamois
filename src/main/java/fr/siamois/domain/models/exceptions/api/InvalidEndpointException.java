package fr.siamois.domain.models.exceptions.api;

public class InvalidEndpointException extends Exception {
    public InvalidEndpointException(String message) {
        super(message);
    }
}
