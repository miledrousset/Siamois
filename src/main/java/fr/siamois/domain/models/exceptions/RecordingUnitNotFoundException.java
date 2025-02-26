package fr.siamois.domain.models.exceptions;

public class RecordingUnitNotFoundException extends RuntimeException {
    public RecordingUnitNotFoundException(String message) {
        super(message);
    }
}
