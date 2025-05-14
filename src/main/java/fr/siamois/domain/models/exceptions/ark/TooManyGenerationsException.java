package fr.siamois.domain.models.exceptions.ark;

import fr.siamois.domain.models.institution.Institution;

public class TooManyGenerationsException extends RuntimeException {
    public TooManyGenerationsException(int maxGeneration, Institution institution) {
        super(String.format("Can't generate valid ARK after %s generations for institution n°%s. " +
                "Consider changing length of ARK", maxGeneration, institution.getId()));
    }
}
