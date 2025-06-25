package fr.siamois.domain.models.exceptions.vocabulary;

import fr.siamois.domain.models.UserInfo;

public class NoConfigForFieldException extends Exception {
    public NoConfigForFieldException(String message) {
        super(message);
    }

    public NoConfigForFieldException(UserInfo userInfo, String fieldCode) {
        super(String.format("User '%s' from '%s' has no config for fieldCode '%s'",
                userInfo.getUser().getName(), userInfo.getInstitution().getName(), fieldCode));
    }
}
