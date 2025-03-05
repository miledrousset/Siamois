package fr.siamois.domain.models.exceptions.recordingunit;

public class FailedRecordingUnitSaveException extends RuntimeException {
    public FailedRecordingUnitSaveException(String message) {
        super(message);
    }
}
