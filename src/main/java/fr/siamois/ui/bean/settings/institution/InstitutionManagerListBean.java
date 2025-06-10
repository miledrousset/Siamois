package fr.siamois.ui.bean.settings.institution;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.auth.pending.PendingPerson;
import fr.siamois.domain.models.events.LoginEvent;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.auth.PendingPersonService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.institution.UserDialogBean;
import fr.siamois.ui.bean.settings.SettingsDatatableBean;
import fr.siamois.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.*;

import static fr.siamois.utils.MessageUtils.displayInfoMessage;
import static fr.siamois.utils.MessageUtils.displayWarnMessage;

@Slf4j
@Component
@SessionScoped
@Getter
@Setter
public class InstitutionManagerListBean implements SettingsDatatableBean {

    private final transient InstitutionService institutionService;
    private final transient PersonService personService;
    private final UserDialogBean userDialogBean;
    private final LangBean langBean;
    private final transient PendingPersonService pendingPersonService;
    private final SessionSettingsBean sessionSettingsBean;
    private Institution institution;
    private transient Map<Person, String> roles;

    private transient Set<Person> members;
    private transient Set<Person> refMembers;
    private String searchInput;

    public InstitutionManagerListBean(InstitutionService institutionService,
                                      PersonService personService,
                                      UserDialogBean userDialogBean,
                                      LangBean langBean,
                                      PendingPersonService pendingPersonService,
                                      SessionSettingsBean sessionSettingsBean) {
        this.institutionService = institutionService;
        this.personService = personService;
        this.userDialogBean = userDialogBean;
        this.langBean = langBean;
        this.pendingPersonService = pendingPersonService;
        this.sessionSettingsBean = sessionSettingsBean;
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

    private static boolean userIsSuperAdmin(Person p) {
        return p.isSuperAdmin();
    }

    public String strRoleOf(Person person) {
        if (userIsSuperAdmin(person)) {
            return "Administrateur";
        } else if (userIsManagerOf(institution, person)) {
            return "Responsable";
        } else {
            return "ERROR";
        }
    }

    public String addDateOf(Person person) {
        if (userIsSuperAdmin(person)) {
            return DateUtils.formatOffsetDateTime(institution.getCreationDate());
        }
        // TODO: Implement a way to get the date when a person was added as a manager
        return "";
    }

    @Override
    public void filter() {
        log.trace("Filtering values with text: {}", searchInput);
        if (searchInput == null || searchInput.isEmpty()) {
            members = new HashSet<>(refMembers);
        } else {
            members = new HashSet<>();
            for (Person person : refMembers) {
                if (person.displayName().toLowerCase().contains(searchInput.toLowerCase())) {
                    members.add(person);
                }
            }
            for (Person person : refMembers) {
                if (person.getEmail().toLowerCase().contains(searchInput.toLowerCase())) {
                    members.add(person);
                }
            }
        }
    }

    @Override
    public void add() {
        log.trace("Creating manager");
        userDialogBean.init(langBean.msg("organisationSettings.managers.add"), langBean.msg("organisationSettings.managers.add"), institution, this::save);
        PrimeFaces.current().ajax().update("newMemberDialog");
        PrimeFaces.current().executeScript("PF('newMemberDialog').show();");
    }

    private void saveUser(UserDialogBean.UserMailRole userMailRole) {
        Optional<Person> existingsUser = personService.findByEmail(userMailRole.getEmail());
        if (existingsUser.isPresent()) {
            boolean isAdded = institutionService.addToManagers(institution, existingsUser.get());
            if (!isAdded) {
                displayWarnMessage(langBean, "organisationSettings.error.manager", existingsUser.get().getEmail(), institution.getName());
                PrimeFaces.current().executeScript("PF('newMemberDialog').showError();");
                return;
            }
            displayInfoMessage(langBean, "organisationSettings.action.addUserToManager", existingsUser.get().getEmail());
            return;
        }

        PendingPerson pendingPerson = pendingPersonService.createOrGetPendingPerson(userMailRole.getEmail());
        if (pendingPersonService.sendPendingManagerInstitutionInvite(pendingPerson, institution, true, sessionSettingsBean.getLanguageCode())) {
            displayInfoMessage(langBean, "organisationSettings.action.sendInvite", pendingPerson.getEmail());
        } else {
            displayInfoMessage(langBean, "organisationSettings.action.addUserToManager", pendingPerson.getEmail());
        }

    }

    public void save() {
        for (UserDialogBean.UserMailRole userMailRole : userDialogBean.getInputUserMailRoles()) {
            if (!userMailRole.isEmpty()) {
                log.trace("Attempting to add user: {}", userMailRole.getEmail());
                saveUser(userMailRole);
            }
        }
        userDialogBean.exit();
    }

    @EventListener(LoginEvent.class)
    public void reset() {
        institution = null;
        members = null;
        refMembers = null;
        roles = null;
        searchInput = null;
    }

}
