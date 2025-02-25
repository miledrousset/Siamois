package fr.siamois.models.exceptions.ark;

import fr.siamois.models.Institution;

public class TooManyGenerationsException extends Exception {
    public TooManyGenerationsException(int maxGeneration, Institution institution) {
        super(String.format("Can't generate valid ARK after %s generations for institution n°%s. " +
                "Consider changing length of ARK", maxGeneration, institution.getId()));
    }
}
