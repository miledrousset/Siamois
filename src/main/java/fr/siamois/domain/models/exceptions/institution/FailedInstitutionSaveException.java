package fr.siamois.domain.models.exceptions.institution;

import jakarta.validation.constraints.NotNull;

public class FailedInstitutionSaveException extends Exception {
    public FailedInstitutionSaveException(@NotNull String s) {
        super(s);
    }
}
