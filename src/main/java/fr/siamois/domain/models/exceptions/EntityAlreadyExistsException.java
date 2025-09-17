package fr.siamois.domain.models.exceptions;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends Exception {
    private final String field; // The unique field not being unique
    public EntityAlreadyExistsException(String field, String message) {
        super(message); this.field = field;
    }
}
