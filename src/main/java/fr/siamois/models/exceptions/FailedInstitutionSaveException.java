package fr.siamois.models.exceptions;

import jakarta.validation.constraints.NotNull;

public class FailedInstitutionSaveException extends Exception {
    public FailedInstitutionSaveException(@NotNull String s) {
        super(s);
    }
}
