package fr.siamois.domain.models.exceptions.actionunit;

public class FailedActionUnitSaveException extends RuntimeException {
    public FailedActionUnitSaveException(String message) {
        super(message);
    }
}
