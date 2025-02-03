package fr.siamois.bean.user;

import fr.siamois.bean.LangBean;
import fr.siamois.models.Institution;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.FailedInstitutionSaveException;
import fr.siamois.models.exceptions.InstitutionAlreadyExist;
import fr.siamois.services.InstitutionService;
import fr.siamois.services.PersonService;
import fr.siamois.services.TeamService;
import fr.siamois.utils.CodeUtils;
import fr.siamois.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * <p>This bean handles the creation of a new team</p>
 * <p>It is used to create a new team and add a manager to it</p>
 *
 * @author Julien Linget
 */
@Slf4j
@Getter
@Setter
@Component
public class InstitutionCreationBean implements Serializable {

    // Injections
    private final TeamService teamService;
    private final PersonService personService;
    private final LangBean langBean;
    private final UserAddBean userAddBean;
    private final InstitutionService institutionService;

    // Storage
    private List<Person> managers;
    private List<Person> filteredManagers;

    // Fields
    private String fTeamName;
    private String fDescription;
    private String fManagerSelectionType;
    private String fTeamDescription;
    private String fInstCode = CodeUtils.generateCode(6);

    private Person fManager;

    public InstitutionCreationBean(TeamService teamService, PersonService personService, LangBean langBean, UserAddBean userAddBean, InstitutionService institutionService) {
        this.teamService = teamService;
        this.personService = personService;
        this.langBean = langBean;
        this.userAddBean = userAddBean;
        this.institutionService = institutionService;
    }

    /**
     * Reset the variables of the bean and load all the managers in the bean
     */
    public void init() {
        loadManagers();
        fTeamName = "";
        fDescription = "";
        fManagerSelectionType = "select";
        fTeamDescription = "";
        fManager = null;
    }

    /**
     * Load all the managers in the bean
     */
    public void loadManagers() {
        managers = institutionService.findAllManagers();
    }


    /**
     * Save the team and the manager in the database
     */
    public void saveTeamAndManager() {
        if (fManagerSelectionType.equalsIgnoreCase("create")) {
            fManager = userAddBean.createUser(true);
        }

        if (fManager != null)  {
            try {
                Institution institution = new Institution();
                institution.setName(fTeamName);
                institution.setDescription(fDescription);
                institution.setManager(fManager);
                institution.setCode(fInstCode);
                institutionService.createInstitution(institution);
                MessageUtils.displayInfoMessage(langBean, "create.team.success");
            } catch (InstitutionAlreadyExist e) {
                log.error("Institution already exists.", e);
                MessageUtils.displayErrorMessage(langBean, "commons.error.team.alreadyexist", fTeamName);
            } catch (FailedInstitutionSaveException e) {
                log.error("Failed to save team.", e);
                MessageUtils.displayErrorMessage(langBean, "commons.error.team.failedsave");
            }
        }
    }

}
