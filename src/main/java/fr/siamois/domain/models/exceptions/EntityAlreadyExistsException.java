package fr.siamois.domain.models.exceptions;

public class EntityAlreadyExistsException extends Exception {
    public EntityAlreadyExistsException(String s) {
        super(s);
    }
}
