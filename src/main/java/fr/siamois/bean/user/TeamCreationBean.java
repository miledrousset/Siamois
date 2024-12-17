package fr.siamois.bean.user;

import fr.siamois.bean.LangBean;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.FailedTeamSaveException;
import fr.siamois.models.exceptions.TeamAlreadyExistException;
import fr.siamois.services.PersonService;
import fr.siamois.services.TeamService;
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
public class TeamCreationBean implements Serializable {

    // Injections
    private final TeamService teamService;
    private final PersonService personService;
    private final LangBean langBean;
    private final UserAddBean userAddBean;

    // Storage
    private List<Person> managers;
    private List<Person> filteredManagers;

    // Fields
    private String fTeamName;
    private String fDescription;
    private String fManagerSelectionType;
    private String fTeamDescription;

    private Person fManager;

    public TeamCreationBean(TeamService teamService, PersonService personService, LangBean langBean, UserAddBean userAddBean) {
        this.teamService = teamService;
        this.personService = personService;
        this.langBean = langBean;
        this.userAddBean = userAddBean;
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
        managers = teamService.findAllManagers();
    }


    /**
     * Save the team and the manager in the database
     */
    public void saveTeamAndManager() {
        if (fManagerSelectionType.equalsIgnoreCase("create")) {
            fManager = userAddBean.createUser();
        }

        if (fManager != null)  {
            try {
                teamService.createTeam(fTeamName, fDescription, fManager);
                MessageUtils.displayInfoMessage(langBean, "create.team.success");
            } catch (TeamAlreadyExistException e) {
                log.error("Team already exists.", e);
                MessageUtils.displayErrorMessage(langBean, "commons.error.team.alreadyexist", fTeamName);
            } catch (FailedTeamSaveException e) {
                log.error("Failed to save team.", e);
                MessageUtils.displayErrorMessage(langBean, "commons.error.team.failedsave");
            }
        }
    }

}
