package fr.siamois.models.exceptions.ark;

import fr.siamois.models.Institution;

public class NoArkConfigException extends Exception {

    public NoArkConfigException(Institution institution) {
        super("No ARK configuration for institution n°" + institution.getId());
    }

}
