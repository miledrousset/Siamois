package fr.siamois.domain.models.exceptions.institution;

public class NullInstitutionIdentifier extends RuntimeException {
    public NullInstitutionIdentifier(String message) {
        super(message);
    }
}
