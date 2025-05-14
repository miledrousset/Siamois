package fr.siamois.domain.models.exceptions.ark;

import fr.siamois.domain.models.institution.Institution;

public class NoArkConfigException extends RuntimeException {

    public NoArkConfigException(Institution institution) {
        super("No ARK configuration for institution n°" + institution.getId());
    }

}
