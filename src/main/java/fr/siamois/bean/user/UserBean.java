package fr.siamois.bean.user;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.NavBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.FailedTeamSaveException;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.FieldConfigurationWrapper;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.TeamTopicSubscriber;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.TeamService;
import fr.siamois.services.vocabulary.FieldService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Getter
@Setter
@SessionScoped
public class UserBean implements Serializable, TeamTopicSubscriber {

    // Injections
    private final SessionSettings sessionSettings;
    private final UserAddBean userAddBean;
    private final TeamService teamService;
    private final FieldConfigurationService fieldConfigurationService;
    private final FieldService fieldService;
    private final LangBean langBean;
    private final NavBean navBean;

    // Storage
    private Vocabulary vocabularyConfiguration;
    private FieldConfigurationWrapper fieldConfig;
    private List<Person> teamMembers;
    private List<ConceptFieldDTO> concepts;

    // Fields
    private ConceptFieldDTO role = null;
    private Team adminTeamSelection = null;

    public UserBean(UserAddBean userAddBean, TeamService teamService, SessionSettings sessionSettings, FieldConfigurationService fieldConfigurationService, FieldService fieldService, LangBean langBean, NavBean navBean) {
        this.userAddBean = userAddBean;
        this.teamService = teamService;
        this.sessionSettings = sessionSettings;
        this.fieldConfigurationService = fieldConfigurationService;
        this.fieldService = fieldService;
        this.langBean = langBean;
        this.navBean = navBean;
    }

    public void init() {
        navBean.addSubscriber(this);
        resetFields();
        loadTeamMembers();
    }

    private void loadTeamMembers() {
        try {
            fieldConfig = fieldConfigurationService.fetchConfigurationOfFieldCode(sessionSettings.getAuthenticatedUser(), Person.USER_ROLE_FIELD_CODE);
            teamMembers = teamService.findTeamMembers(sessionSettings.getSelectedTeam());
            log.trace("Team members : {}", teamMembers);
        } catch (NoConfigForField e) {
            log.trace("Error while loading member configuration", e);
        }
    }

    public List<String> autocompleteRoles(String input) throws NoConfigForField {
        if (fieldConfig == null) fieldConfig = fieldConfigurationService.fetchConfigurationOfFieldCode(sessionSettings.getAuthenticatedUser(), Person.USER_ROLE_FIELD_CODE);
        concepts = fieldService.fetchAutocomplete(fieldConfig, input, langBean.getLanguageCode());
        return concepts.stream().map(ConceptFieldDTO::getLabel).collect(Collectors.toList());
    }

    private void resetFields() {
        vocabularyConfiguration = null;
        fieldConfig = null;
        concepts = new ArrayList<>();
    }

    public void createUser() {
        Concept roleConcept = fieldService.saveConceptIfNotExist(fieldConfig, role);
        Team team = sessionSettings.getSelectedTeam();
        Person created = userAddBean.createUser();
        try {
            teamService.addUserToTeam(created, team, roleConcept);
        } catch (FailedTeamSaveException e) {
            // TODO: Afficher un message d'erreur
        }
    }

    @Override
    public void onTeamChange() {
        loadTeamMembers();
    }
}
