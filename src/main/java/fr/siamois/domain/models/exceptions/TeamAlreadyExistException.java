package fr.siamois.domain.models.exceptions;

import jakarta.validation.constraints.NotNull;

public class TeamAlreadyExistException extends Exception {
    public TeamAlreadyExistException(@NotNull String teamName, String institution) {
        super("Team with name " + teamName + " already exists in institution " + institution);
    }
}
