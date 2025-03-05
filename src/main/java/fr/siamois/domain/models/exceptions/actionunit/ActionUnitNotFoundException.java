package fr.siamois.domain.models.exceptions.actionunit;

public class ActionUnitNotFoundException extends RuntimeException {
    public ActionUnitNotFoundException(String message) {
        super(message);
    }
}
