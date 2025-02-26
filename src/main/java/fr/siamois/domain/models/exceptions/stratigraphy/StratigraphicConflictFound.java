package fr.siamois.domain.models.exceptions.stratigraphy;

public class StratigraphicConflictFound extends RuntimeException {
    public StratigraphicConflictFound(String message) {
        super(message);
    }
}
