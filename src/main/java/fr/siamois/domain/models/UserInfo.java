package fr.siamois.domain.models;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
import lombok.Getter;

import java.io.Serializable;


@Getter
public class UserInfo implements Serializable {

    protected final Institution institution;
    protected final Person user;
    protected final String lang;

    public UserInfo(Institution institution, Person user, String lang) {
        this.institution = institution;
        this.user = user;
        this.lang = lang;
    }

}
