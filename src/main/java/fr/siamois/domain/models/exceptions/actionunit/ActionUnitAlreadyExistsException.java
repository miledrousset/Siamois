package fr.siamois.domain.models.exceptions.actionunit;

import fr.siamois.domain.models.exceptions.EntityAlreadyExistsException;

public class ActionUnitAlreadyExistsException extends EntityAlreadyExistsException {
    public ActionUnitAlreadyExistsException(String name, String message) {
        super(name, message);
    }
}
