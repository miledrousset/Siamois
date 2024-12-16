package fr.siamois.models.exceptions;

public class NoTeamSelectedException extends Exception {
    public NoTeamSelectedException() {
        super("No team selected");
    }
}
