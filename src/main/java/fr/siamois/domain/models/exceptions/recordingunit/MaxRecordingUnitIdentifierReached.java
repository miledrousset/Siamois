package fr.siamois.domain.models.exceptions.recordingunit;

public class MaxRecordingUnitIdentifierReached extends RuntimeException {
    public MaxRecordingUnitIdentifierReached(String message) {
        super(message);
    }
}
