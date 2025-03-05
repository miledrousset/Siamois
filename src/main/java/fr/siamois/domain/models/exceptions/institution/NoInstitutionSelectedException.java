package fr.siamois.domain.models.exceptions.institution;

public class NoInstitutionSelectedException extends IllegalStateException {
    public NoInstitutionSelectedException(String s) {
        super(s);
    }
}
