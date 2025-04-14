package fr.siamois.ui.bean.settings.institution;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.person.PersonService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@SessionScoped
@Getter
@Setter
public class InstitutionManagerSettingsBean implements Serializable {

    private final transient InstitutionService institutionService;
    private final transient PersonService personService;
    private Institution institution;
    private Map<Person, String> roles;

    private Set<Person> members;

    public InstitutionManagerSettingsBean(InstitutionService institutionService, PersonService personService) {
        this.institutionService = institutionService;
        this.personService = personService;
    }

    public void init(Institution institution) {
        this.institution = institution;
        members = new HashSet<>();
        for (Person member : institutionService.findMembersOf(institution)) {
            String name = strRoleOf(member);
            if (!name.equalsIgnoreCase("ERROR")) {
                members.add(member);
            }
        }
    }

    private boolean userIsManagerOf(Institution institution, Person p) {
        return institutionService.isManagerOf(institution, p);
    }

    private static boolean userIsOwnerOf(Institution institution, Person p) {
        return p.getId().equals(institution.getManager().getId());
    }

    private static boolean userIsSuperAdmin(Person p) {
        return p.isSuperAdmin();
    }

    public String strRoleOf(Person person) {
        if (userIsSuperAdmin(person)) {
            return "Administrateur";
        } else if (userIsOwnerOf(institution, person)) {
            return "Propriétaire";
        } else if (userIsManagerOf(institution, person)) {
            return "Responsable";
        } else {
            return "ERROR";
        }
    }

}
