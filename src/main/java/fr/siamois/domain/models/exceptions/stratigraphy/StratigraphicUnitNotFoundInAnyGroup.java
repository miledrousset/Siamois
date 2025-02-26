package fr.siamois.domain.models.exceptions.stratigraphy;

public class StratigraphicUnitNotFoundInAnyGroup extends RuntimeException {
    public StratigraphicUnitNotFoundInAnyGroup(String message) {
        super(message);
    }
}
