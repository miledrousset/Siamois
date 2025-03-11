package fr.siamois.domain.models.exceptions;

public class InvalidFileSizeException extends Exception {
    public InvalidFileSizeException(Long size, String message) {
        super("Invalid file size " + size + ": " + message);
    }
}
