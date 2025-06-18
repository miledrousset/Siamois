package fr.siamois.ui.bean.dialog.institution;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.events.LoginEvent;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.ActionFromBean;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.email.EmailManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
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

    private TabState tabState = TabState.SEARCH;

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
        this.tabState = TabState.SEARCH;
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
        this.tabState = TabState.SEARCH;
    }

    /**
     * Creates and saves a new Person in the database.
     * @return the list of created or found Person objects. If a single Person is created or found, it will be returned as a single-element list.
     */
    public List<PersonRole> createOrSearchPersons() {
        return switch (tabState) {
            case SEARCH -> List.of(searchPerson());
            case CREATE -> List.of(createPerson());
            case BULK -> throw new UnsupportedOperationException("Bulk creation is not implemented yet.");
        };
    }

    public PersonRole searchPerson() {
        // TODO: Implement search logic
        return null;
    }

    public PersonRole createPerson() {
        // TODO: Implement creation logic
        return null;
    }

    public void exit() {
        reset();
        PrimeFaces.current().executeScript("PF('newMemberDialog').hide();");
    }

    public void tabChanged(TabChangeEvent<Void> event) {
        TabState oldState = tabState;
        String tabId = event.getTab().getId();
        switch (tabId) {
            case "createNewUser" -> tabState = TabState.CREATE;
            case "importUserGroup" -> tabState = TabState.BULK;
            default -> tabState = TabState.SEARCH;
        }
        log.trace("Tab changed from {} to: {}", oldState, tabState);
    }

    public record PersonRole(
            Person person,
            Concept role
    ) {}

    public enum TabState {
        SEARCH,
        CREATE,
        BULK
    }

}
