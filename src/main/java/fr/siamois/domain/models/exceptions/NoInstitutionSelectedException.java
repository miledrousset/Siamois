package fr.siamois.domain.models.exceptions;

public class NoInstitutionSelectedException extends IllegalStateException {
    public NoInstitutionSelectedException(String s) {
        super(s);
    }
}
