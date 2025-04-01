package fr.siamois.ui.bean.user;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.institution.FailedInstitutionSaveException;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.FieldService;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.NavBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This bean handles the user management page</p>
 *
 * @author Julien Linget
 */
@Slf4j
@Component
@Getter
@Setter
@SessionScoped
public class UserBean implements Serializable {

    // Injections
    private final SessionSettingsBean sessionSettingsBean;
    private final UserAddBean userAddBean;
    private final transient FieldService fieldService;
    private final LangBean langBean;
    private final NavBean navBean;
    private final transient InstitutionService institutionService;
    private final transient ConceptService conceptService;
    private final transient FieldConfigurationService fieldConfigurationService;

    // Storage
    private Vocabulary vocabularyConfiguration;
    private List<Person> teamMembers;
    private List<Concept> concepts;

    // Fields
    private Concept role = null;
    private Institution adminInstitutionSelection = null;

    public UserBean(UserAddBean userAddBean,
                    SessionSettingsBean sessionSettingsBean,
                    FieldService fieldService,
                    LangBean langBean,
                    NavBean navBean, InstitutionService institutionService, ConceptService conceptService, FieldConfigurationService fieldConfigurationService) {
        this.userAddBean = userAddBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.fieldService = fieldService;
        this.langBean = langBean;
        this.navBean = navBean;
        this.institutionService = institutionService;
        this.conceptService = conceptService;
        this.fieldConfigurationService = fieldConfigurationService;
    }

    /**
     * Initialize the bean. Reset the fields and load the team members
     */
    public void init() {
        resetFields();
        loadTeamMembers();
    }

    /**
     * Load the team members
     */
    private void loadTeamMembers() {
        teamMembers = institutionService.findMembersOf(sessionSettingsBean.getSelectedInstitution());
        log.trace("Team members : {}", teamMembers);
    }

    /**
     * Autocomplete the roles
     *
     * @param input the input to autocomplete
     * @return the list of roles
     * @throws NoConfigForFieldException if the field configuration is not found
     */
    public List<String> autocompleteRoles(String input) throws NoConfigForFieldException {
        concepts = fieldConfigurationService.fetchAutocomplete(sessionSettingsBean.getUserInfo(), Person.USER_ROLE_FIELD_CODE, input);
        return concepts.stream().map(Concept::getLabel).toList();
    }

    /**
     * Reset the fields
     */
    private void resetFields() {
        vocabularyConfiguration = null;
        concepts = new ArrayList<>();
    }

    /**
     * Create a user
     */
    public void createUser() {
        Concept roleConcept = conceptService.saveOrGetConcept(role);
        try {
            Institution institution = sessionSettingsBean.getSelectedInstitution();
            Person created = userAddBean.createUser(false);
            institutionService.addUserToInstitution(created, institution, roleConcept);
        } catch (FailedInstitutionSaveException e) {
            log.error("Error while saving team", e);
            MessageUtils.displayErrorMessage(langBean, "commons.error.team.save");
        }
    }

    public Person loggedUser() {
        return sessionSettingsBean.getAuthenticatedUser();
    }

}
