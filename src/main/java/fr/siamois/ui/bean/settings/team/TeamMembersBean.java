package fr.siamois.ui.bean.settings.team;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.auth.pending.PendingPerson;
import fr.siamois.domain.models.team.TeamMemberRelation;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.auth.PendingPersonService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.ui.bean.LabelBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.institution.UserDialogBean;
import fr.siamois.ui.bean.settings.SettingsDatatableBean;
import fr.siamois.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class TeamMembersBean implements SettingsDatatableBean {

    private final LabelBean labelBean;
    private final transient InstitutionService institutionService;
    private final UserDialogBean userDialogBean;
    private final PersonService personService;
    private final PendingPersonService pendingPersonService;
    private final SessionSettingsBean sessionSettingsBean;
    private ActionUnit actionUnit;

    private String searchInput;

    private Set<TeamMemberRelation> memberRelations;
    private List<TeamMemberRelation> filteredMemberRelations;

    public TeamMembersBean(LabelBean labelBean, InstitutionService institutionService, UserDialogBean userDialogBean, PersonService personService, PendingPersonService pendingPersonService, SessionSettingsBean sessionSettingsBean) {
        this.labelBean = labelBean;
        this.institutionService = institutionService;
        this.userDialogBean = userDialogBean;
        this.personService = personService;
        this.pendingPersonService = pendingPersonService;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    public void reset() {
        this.actionUnit = null;
        this.memberRelations = null;
        this.filteredMemberRelations = null;
    }

    public void init(ActionUnit actionUnit) {
        this.actionUnit = actionUnit;
        this.memberRelations = institutionService.findRelationsOf(actionUnit);
        this.filteredMemberRelations = new ArrayList<>(memberRelations);
    }

    @Override
    public void add() {
        userDialogBean.init("Ajouter des membres", "Ajouter", actionUnit.getCreatedByInstitution(), this::saveAll);
        userDialogBean.setShouldRenderRoleField(true);
        PrimeFaces.current().ajax().update("userDialogBeanForm:newMemberDialog");
        PrimeFaces.current().executeScript("PF('newMemberDialog').show();");
    }

    private void save(UserDialogBean.UserMailRole userMailRole) {
        if (userMailRole.getEmail() == null || userMailRole.getEmail().isEmpty()) {
            return; // Skip saving if email is empty
        }

        Optional<Person> optPerson = personService.findByEmail(userMailRole.getEmail());
        if (optPerson.isPresent()) {
            Person person = optPerson.get();
            if (institutionService.addPersonToActionUnit(actionUnit, person, userMailRole.getRole())) {
                log.debug("Added person {} with role {} to action unit {}", person.getName(), userMailRole.getRole(), actionUnit.getName());
            } else {
                log.warn("Person {} is already a member of action unit {}", person.getName(), actionUnit.getName());
            }
        } else {
            PendingPerson pendingPerson = pendingPersonService.createOrGetPendingPerson(userMailRole.getEmail());
            if (pendingPersonService.sendPendingActionMemberInvite(pendingPerson, actionUnit, userMailRole.getRole(), sessionSettingsBean.getLanguageCode())) {
                log.debug("Sent invite to pending person {} for action unit {}", pendingPerson.getEmail(), actionUnit.getName());
            } else {
                log.warn("Pending person {} already has an invite for action unit {}", pendingPerson.getEmail(), actionUnit.getName());
            }
        }

    }

    public void saveAll() {
        for (UserDialogBean.UserMailRole userMailRole :  userDialogBean.getInputUserMailRoles()) {
            save(userMailRole);
        }
        PrimeFaces.current().executeScript("PF('newMemberDialog').hide();");
    }

    @Override
    public void filter() {
        if (searchInput == null || searchInput.isEmpty()) {
            filteredMemberRelations = new ArrayList<>(memberRelations);
        } else {
            filteredMemberRelations = memberRelations.stream()
                    .filter(relation -> relation.getPerson().getName().toLowerCase().contains(searchInput.toLowerCase()))
                    .toList();
        }
    }

    public String formatRole(TeamMemberRelation relation) {
        if (relation.getRole() == null) {
            return "";
        }
        return labelBean.findLabelOf(relation.getRole());
    }

    public String formatDate(TeamMemberRelation relation) {
        return DateUtils.formatOffsetDateTime(relation.getAddedAt());
    }
}
