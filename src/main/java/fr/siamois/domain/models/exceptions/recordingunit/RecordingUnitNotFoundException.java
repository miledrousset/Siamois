package fr.siamois.domain.models.exceptions.recordingunit;

public class RecordingUnitNotFoundException extends RuntimeException {
    public RecordingUnitNotFoundException(String message) {
        super(message);
    }
}
