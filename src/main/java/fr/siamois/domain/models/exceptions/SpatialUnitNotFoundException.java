package fr.siamois.domain.models.exceptions;

public class SpatialUnitNotFoundException extends RuntimeException {
    public SpatialUnitNotFoundException(String message) {
        super(message);
    }
}
