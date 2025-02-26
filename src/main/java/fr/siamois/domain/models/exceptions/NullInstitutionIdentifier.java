package fr.siamois.domain.models.exceptions;

public class NullInstitutionIdentifier extends RuntimeException {
    public NullInstitutionIdentifier(String message) {
        super(message);
    }
}
