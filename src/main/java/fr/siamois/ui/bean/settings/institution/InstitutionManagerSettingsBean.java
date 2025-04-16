package fr.siamois.ui.bean.settings.institution;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.settings.PersonRoleInstitution;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.ui.bean.dialog.institution.UserDialogBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@SessionScoped
@Getter
@Setter
public class InstitutionManagerSettingsBean implements Serializable {

    private final transient InstitutionService institutionService;
    private final transient PersonService personService;
    private final UserDialogBean userDialogBean;
    private Institution institution;
    private transient Map<Person, String> roles;

    private transient Set<Person> members;
    private transient Set<Person> refMembers;
    private String textSearch;

    public InstitutionManagerSettingsBean(InstitutionService institutionService, PersonService personService, UserDialogBean userDialogBean) {
        this.institutionService = institutionService;
        this.personService = personService;
        this.userDialogBean = userDialogBean;
    }

    public void init(Institution institution) {
        this.institution = institution;
        refMembers = new HashSet<>();
        roles = new HashMap<>();
        for (Person member : institutionService.findMembersOf(institution)) {
            String name = strRoleOf(member);
            if (!name.equalsIgnoreCase("ERROR")) {
                refMembers.add(member);
                roles.put(member, name);
            }
        }
        members = new HashSet<>(refMembers);
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

    public String addDateOf(Person person) {
        if (userIsOwnerOf(institution, person) || userIsSuperAdmin(person)) {
            return DateUtils.formatOffsetDateTime(institution.getCreationDate());
        }

        PersonRoleInstitution result =  institutionService.findPersonInInstitution(institution, person).orElseThrow(() ->
                new IllegalStateException("User should exist"));
        return DateUtils.formatOffsetDateTime(result.getAddedAt());
    }

    public void filterValues() {
        log.trace("Filtering values with text: {}", textSearch);
        if (textSearch == null || textSearch.isEmpty()) {
            members = new HashSet<>(refMembers);
        } else {
            members = new HashSet<>();
            for (Person person : refMembers) {
                if (person.displayName().toLowerCase().contains(textSearch.toLowerCase())) {
                    members.add(person);
                }
            }
            for (Person person : refMembers) {
                if (person.getMail().toLowerCase().contains(textSearch.toLowerCase())) {
                    members.add(person);
                }
            }
        }
    }

    public void createManager() {
        log.trace("Creating manager");
        // Display create manager dialog
        userDialogBean.init("Créer une responsable", "Créer un responsable", institution);
        PrimeFaces.current().executeScript("PF('newManagerDialog').show();");
    }

}
