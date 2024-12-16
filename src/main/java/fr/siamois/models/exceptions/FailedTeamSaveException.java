package fr.siamois.models.exceptions;

import jakarta.validation.constraints.NotNull;

public class FailedTeamSaveException extends Exception {
    public FailedTeamSaveException(@NotNull String s) {
        super(s);
    }
}
