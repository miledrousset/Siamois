package fr.siamois.ui.bean.dialog.institution;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.events.LoginEvent;
import fr.siamois.domain.models.exceptions.auth.*;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@SessionScoped
@Getter
@Setter
public class UserDialogBean implements Serializable {

    // Injections
    private final transient EmailManager emailManager;
    private final transient PersonService personService;
    private final transient InstitutionService institutionService;
    private final LangBean langBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final SessionSettingsBean sessionSettingsBean;

    // Data storage
    private Institution institution;
    private transient ActionFromBean actionFromBean;
    private String title;
    private String buttonLabel;
    private List<Person> alreadyExistingPersons = new ArrayList<>();

    private Concept roleParentConcept;
    private boolean shouldRenderRoleField = false;

    private TabState tabState = TabState.SEARCH;

    // Search TAB
    private Person selectedExistingPerson;
    private List<Person> personSelectedList = new ArrayList<>();

    // Create TAB
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

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
        this.selectedExistingPerson = null;
        this.personSelectedList.clear();
        this.alreadyExistingPersons.clear();
        this.firstname = null;
        this.lastname = null;
        this.username = null;
        this.email = null;
        this.password = null;
        this.confirmPassword = null;
    }

    /**
     * Creates and saves a new Person in the database.
     * @return the list of created or found Person objects. If a single Person is created or found, it will be returned as a single-element list.
     */
    public List<PersonRole> createOrSearchPersons() {
        return switch (tabState) {
            case SEARCH -> searchPerson();
            case CREATE -> List.of(createPerson());
            case BULK -> throw new UnsupportedOperationException("Bulk creation is not implemented yet.");
        };
    }

    public List<PersonRole> searchPerson() {
        return personSelectedList
                .stream()
                .map(p -> new PersonRole(p, null))
                .toList();
    }

    public PersonRole createPerson() {
        if (firstname == null || lastname == null || username == null || email == null || password == null || confirmPassword == null) {
            log.error("All fields must be filled out.");
            return null;
        }

        if (!password.equals(confirmPassword)) {
            log.error("Password and confirmation do not match.");
            return null;
        }

        Person person = new Person();
        person.setName(firstname);
        person.setLastname(lastname);
        person.setUsername(username);
        person.setEmail(email);
        person.setPassword(password);
        person.setPassToModify(true);

        try {
            return new PersonRole(personService.createPerson(person), null);
        } catch (InvalidUsernameException e) {
            log.error("Invalid username: {}", e.getMessage());
        } catch (InvalidEmailException e) {
            log.error("Invalid email: {}", e.getMessage());
        } catch (UserAlreadyExistException e) {
            log.error("User already exists: {}", e.getMessage());
        } catch (InvalidPasswordException e) {
            log.error("Invalid password: {}", e.getMessage());
        } catch (InvalidNameException e) {
            log.error("Invalid name: {}", e.getMessage());
        }
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

    public List<Person> searchUser(String usernameOrMailInput) {
        List<Person> result = new ArrayList<>(personService.findClosestByUsernameOrEmail(usernameOrMailInput));
        for (Person person : alreadyExistingPersons) {
            result.remove(person);
        }

        for (Person person : personSelectedList) {
            result.remove(person);
        }

        return result;
    }

    public void addToList() {
        if (selectedExistingPerson != null) {
            personSelectedList.add(selectedExistingPerson);
            selectedExistingPerson = null;
        }
    }

    public void removeFromList(Person person) {
        personSelectedList.remove(person);
    }

}
