package fr.siamois.models.exceptions.field;

public class FailedFieldSaveException extends RuntimeException  {

    public FailedFieldSaveException(String message) {
        super(message);
    }
}
