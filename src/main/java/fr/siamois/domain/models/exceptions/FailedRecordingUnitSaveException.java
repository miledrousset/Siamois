package fr.siamois.domain.models.exceptions;

public class FailedRecordingUnitSaveException extends RuntimeException {
    public FailedRecordingUnitSaveException(String message) {
        super(message);
    }
}
