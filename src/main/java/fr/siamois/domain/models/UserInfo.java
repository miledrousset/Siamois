package fr.siamois.domain.models;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.institution.Institution;
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

    public boolean isInSuperadminInstitution() {
        return institution.getIdentifier().equalsIgnoreCase("SIAMOIS");
    }

}
