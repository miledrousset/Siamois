package fr.siamois.bean.user;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.NavBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.models.Institution;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.FailedInstitutionSaveException;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.InstitutionService;
import fr.siamois.services.vocabulary.ConceptService;
import fr.siamois.services.vocabulary.FieldService;
import fr.siamois.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final SessionSettings sessionSettings;
    private final UserAddBean userAddBean;
    private final FieldService fieldService;
    private final LangBean langBean;
    private final NavBean navBean;
    private final InstitutionService institutionService;
    private final ConceptService conceptService;

    // Storage
    private Vocabulary vocabularyConfiguration;
    private List<Person> teamMembers;
    private List<Concept> concepts;

    // Fields
    private Concept role = null;
    private Institution adminInstitutionSelection = null;

    public UserBean(UserAddBean userAddBean,
                    SessionSettings sessionSettings,
                    FieldService fieldService,
                    LangBean langBean,
                    NavBean navBean, InstitutionService institutionService, ConceptService conceptService) {
        this.userAddBean = userAddBean;
        this.sessionSettings = sessionSettings;
        this.fieldService = fieldService;
        this.langBean = langBean;
        this.navBean = navBean;
        this.institutionService = institutionService;
        this.conceptService = conceptService;
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
        teamMembers = institutionService.findMembersOf(sessionSettings.getSelectedInstitution());
        log.trace("Team members : {}", teamMembers);
    }

    /**
     * Autocomplete the roles
     *
     * @param input the input to autocomplete
     * @return the list of roles
     * @throws NoConfigForField if the field configuration is not found
     */
    public List<String> autocompleteRoles(String input) throws NoConfigForField {
        concepts = conceptService.fetchAutocomplete(sessionSettings.getUserInfo(), Person.USER_ROLE_FIELD_CODE, input);
        return concepts.stream().map(Concept::getLabel).collect(Collectors.toList());
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
            Institution institution = sessionSettings.getSelectedInstitution();
            Person created = userAddBean.createUser(false);
            institutionService.addUserToInstitution(created, institution, roleConcept);
        } catch (FailedInstitutionSaveException e) {
            log.error("Error while saving team", e);
            MessageUtils.displayErrorMessage(langBean, "commons.error.team.save");
        }
    }

}
