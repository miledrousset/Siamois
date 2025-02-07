package fr.siamois.models;

import fr.siamois.models.auth.Person;
import lombok.Getter;

@Getter
public class UserInfo {

    private final Institution institution;
    private final Person user;
    private final String lang;

    public UserInfo(Institution institution, Person user, String lang) {
        this.institution = institution;
        this.user = user;
        this.lang = lang;
    }

}
