package fr.siamois.domain.models.exceptions.spatialunit;

public class SpatialUnitNotFoundException extends RuntimeException {
    public SpatialUnitNotFoundException(String message) {
        super(message);
    }
}
