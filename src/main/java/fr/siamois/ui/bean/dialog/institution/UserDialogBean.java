package fr.siamois.ui.bean.dialog.institution;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.events.LoginEvent;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.ActionFromBean;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.email.EmailManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@SessionScoped
@Getter
@Setter
public class UserDialogBean implements Serializable {

    private final transient EmailManager emailManager;
    private final transient PersonService personService;
    private final transient InstitutionService institutionService;
    private final LangBean langBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final SessionSettingsBean sessionSettingsBean;

    private transient ActionFromBean actionFromBean;

    private Institution institution;
    private String title;
    private String buttonLabel;

    private Concept roleParentConcept;
    private boolean shouldRenderRoleField = false;

    private List<UserMailRole> inputUserMailRoles;

    public UserDialogBean(EmailManager emailManager, PersonService personService, InstitutionService institutionService, LangBean langBean, FieldConfigurationService fieldConfigurationService, SessionSettingsBean sessionSettingsBean) {
        this.emailManager = emailManager;
        this.personService = personService;
        this.institutionService = institutionService;
        this.langBean = langBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.sessionSettingsBean = sessionSettingsBean;
    }

    public void init(String title, String buttonLabel, Institution institution, ActionFromBean actionFromBean) {
        reset();
        this.title = title;
        this.buttonLabel = buttonLabel;
        this.institution = institution;
        this.shouldRenderRoleField = false;
        this.actionFromBean = actionFromBean;
    }

    public void init(String title, String buttonLabel, Institution institution, boolean shouldRenderRole, ActionFromBean actionFromBean) {
        reset();
        this.title = title;
        this.buttonLabel = buttonLabel;
        this.institution = institution;
        this.shouldRenderRoleField = shouldRenderRole;
        this.actionFromBean = actionFromBean;
    }

    @EventListener(LoginEvent.class)
    public void reset() {
        this.institution = null;
        this.title = null;
        this.buttonLabel = null;
        this.shouldRenderRoleField = false;
        this.actionFromBean = null;
        inputUserMailRoles = new ArrayList<>();
        inputUserMailRoles.add(new UserMailRole());
        inputUserMailRoles.add(new UserMailRole());
        inputUserMailRoles.add(new UserMailRole());
    }

    public void exit() {
        reset();
        PrimeFaces.current().executeScript("PF('newMemberDialog').hide();");
    }

    public String parentConceptUrl() {
        return fieldConfigurationService.getUrlForFieldCode(sessionSettingsBean.getUserInfo(), Person.USER_ROLE_FIELD_CODE);
    }

    public List<Concept> autocompleteConcept(String input) {
        UserInfo info = sessionSettingsBean.getUserInfo();

        if (roleParentConcept == null) {
            try {
                roleParentConcept = fieldConfigurationService.findConfigurationForFieldCode(info, Person.USER_ROLE_FIELD_CODE);
            } catch (NoConfigForFieldException e) {
                MessageUtils.displayNoThesaurusConfiguredMessage(langBean);
                return List.of();
            }
        }

        return fieldConfigurationService.fetchConceptChildrenAutocomplete(info, roleParentConcept, input);
    }

    public void removeRow(UserMailRole userMailRole) {
        if (inputUserMailRoles.size() > 1) {
            inputUserMailRoles.remove(userMailRole);
        }
    }

    public void addRow() {
        inputUserMailRoles.add(new UserMailRole());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserMailRole implements Serializable {
        private String email;
        private Concept role;

        public boolean isEmpty() {
            return email == null || email.isEmpty();
        }
    }


}
