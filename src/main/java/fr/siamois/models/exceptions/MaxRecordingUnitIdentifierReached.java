package fr.siamois.models.exceptions;

public class MaxRecordingUnitIdentifierReached extends RuntimeException {
    public MaxRecordingUnitIdentifierReached(String message) {
        super(message);
    }
}
