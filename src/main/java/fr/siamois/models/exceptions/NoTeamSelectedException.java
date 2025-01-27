package fr.siamois.models.exceptions;

public class NoTeamSelectedException extends RuntimeException {
    public NoTeamSelectedException() {
        super("No team selected");
    }
}
