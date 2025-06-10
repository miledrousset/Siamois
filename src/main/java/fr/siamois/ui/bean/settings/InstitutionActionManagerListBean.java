package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.auth.pending.PendingPerson;
import fr.siamois.domain.models.events.LoginEvent;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.team.ActionManagerRelation;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.auth.PendingPersonService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.institution.UserDialogBean;
import fr.siamois.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static fr.siamois.utils.MessageUtils.displayInfoMessage;
import static fr.siamois.utils.MessageUtils.displayWarnMessage;

@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class InstitutionActionManagerListBean implements SettingsDatatableBean {

    private final transient InstitutionService institutionService;
    private final UserDialogBean userDialogBean;
    private final LangBean langBean;
    private final transient PersonService personService;
    private final transient PendingPersonService pendingPersonService;
    private final SessionSettingsBean sessionSettingsBean;
    private Institution institution;
    private transient Set<ActionManagerRelation> refActionManagers;
    private transient List<ActionManagerRelation> filteredActionManagers;

    private String searchInput;

    public InstitutionActionManagerListBean(InstitutionService institutionService, UserDialogBean userDialogBean, LangBean langBean, PersonService personService, PendingPersonService pendingPersonService, SessionSettingsBean sessionSettingsBean) {
        this.institutionService = institutionService;
        this.userDialogBean = userDialogBean;
        this.langBean = langBean;
        this.personService = personService;
        this.pendingPersonService = pendingPersonService;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    @EventListener(LoginEvent.class)
    public void reset() {
        this.institution = null;
        this.refActionManagers = null;
        this.filteredActionManagers = null;
    }

    public void init(Institution institution) {
        this.institution = institution;
        this.refActionManagers = institutionService.findAllActionManagersOf(institution);
        this.filteredActionManagers = new ArrayList<>(refActionManagers);
    }

    @Override
    public void add() {
        userDialogBean.init(langBean.msg("organisationSettings.managers.add"),
                langBean.msg("organisationSettings.managers.add"),
                institution, this::saveUsers);

        PrimeFaces.current().ajax().update("newMemberDialog");
        PrimeFaces.current().executeScript("PF('newMemberDialog').show();");
    }

    @Override
    public void filter() {
        if (searchInput == null || searchInput.isEmpty()) {
            filteredActionManagers = new ArrayList<>(refActionManagers);
        } else {
            filteredActionManagers.clear();

            for (ActionManagerRelation relation : refActionManagers) {
                if (relation.getPerson().displayName().toLowerCase().contains(searchInput.toLowerCase())) {
                    filteredActionManagers.add(relation);
                }
            }

            for (ActionManagerRelation relation : refActionManagers) {
                if (relation.getPerson().getEmail().toLowerCase().contains(searchInput.toLowerCase())) {
                    filteredActionManagers.add(relation);
                }
            }
        }
    }

    private void saveUser(UserDialogBean.UserMailRole userMailRole) {
        Optional<Person> existingsUser = personService.findByEmail(userMailRole.getEmail());
        if (existingsUser.isPresent()) {
            boolean isAdded = institutionService.addPersonToActionManager(institution, existingsUser.get());
            if (!isAdded) {
                displayWarnMessage(langBean, "organisationSettings.error.manager", existingsUser.get().getEmail(), institution.getName());
                PrimeFaces.current().executeScript("PF('newMemberDialog').showError();");
                return;
            }
            displayInfoMessage(langBean, "organisationSettings.action.addUserToManager", existingsUser.get().getEmail());
            return;
        }

        PendingPerson pendingPerson = pendingPersonService.createOrGetPendingPerson(userMailRole.getEmail());
        if (pendingPersonService.sendPendingActionManagerInstitutionInvite(pendingPerson, institution, sessionSettingsBean.getLanguageCode())) {
            displayInfoMessage(langBean, "organisationSettings.action.sendInvite", pendingPerson.getEmail());
        } else {
            displayInfoMessage(langBean, "organisationSettings.action.addUserToManager", pendingPerson.getEmail());
        }
    }

    public void saveUsers() {
        for (UserDialogBean.UserMailRole userMailRole : userDialogBean.getInputUserMailRoles()) {
            if (!userMailRole.isEmpty()) {
                log.trace("Attempting to add user: {}", userMailRole.getEmail());
                saveUser(userMailRole);
            }
        }
        userDialogBean.exit();
    }

    public String formatDate(ActionManagerRelation relation) {
        return DateUtils.formatOffsetDateTime(relation.getAddedAt());
    }

}
