package fr.siamois.ui.exceptions;

import lombok.Getter;

@Getter
public class CannotInitializeNewUnitDialogException extends Exception {
    public CannotInitializeNewUnitDialogException(String message) {
        super(message);
    }
}
