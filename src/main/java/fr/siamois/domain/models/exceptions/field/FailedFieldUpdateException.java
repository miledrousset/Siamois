package fr.siamois.domain.models.exceptions.field;

public class FailedFieldUpdateException extends Exception {
    public FailedFieldUpdateException(String message) {
        super(message);
    }
}
