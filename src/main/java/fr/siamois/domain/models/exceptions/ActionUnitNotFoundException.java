package fr.siamois.domain.models.exceptions;

public class ActionUnitNotFoundException extends RuntimeException {
    public ActionUnitNotFoundException(String message) {
        super(message);
    }
}
