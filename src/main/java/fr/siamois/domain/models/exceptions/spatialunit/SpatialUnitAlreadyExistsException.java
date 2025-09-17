package fr.siamois.domain.models.exceptions.spatialunit;

import fr.siamois.domain.models.exceptions.EntityAlreadyExistsException;

public class SpatialUnitAlreadyExistsException extends EntityAlreadyExistsException {
    public SpatialUnitAlreadyExistsException(String field, String s) {
        super(field, s);
    }
}
