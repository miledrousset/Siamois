package fr.siamois.domain.models.exceptions;

public class MaxRecordingUnitIdentifierReached extends RuntimeException {
    public MaxRecordingUnitIdentifierReached(String message) {
        super(message);
    }
}
