package fr.siamois.bean.user;

import fr.siamois.bean.LangBean;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.FailedTeamSaveException;
import fr.siamois.models.exceptions.TeamAlreadyExistException;
import fr.siamois.models.exceptions.UserAlreadyExist;
import fr.siamois.models.exceptions.auth.InvalidEmail;
import fr.siamois.models.exceptions.auth.InvalidPassword;
import fr.siamois.models.exceptions.auth.InvalidUsername;
import fr.siamois.services.PersonService;
import fr.siamois.services.TeamService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

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

    public void init() {
        loadManagers();
        fTeamName = "";
        fDescription = "";
        fManagerSelectionType = "select";
        fTeamDescription = "";
        fManager = null;
    }

    public void loadManagers() {
        managers = teamService.findAllManagers();
    }

    private void displayMessage(FacesMessage.Severity severity, String title, String message) {
        FacesMessage facesMessage = new FacesMessage(severity, title, message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    public void saveTeamAndManager() {
        if (fManagerSelectionType.equalsIgnoreCase("create")) {
            fManager = userAddBean.createUser();
        }

        if (fManager != null)  {
            try {
                teamService.createTeam(fTeamName, fDescription, fManager);
                displayMessage(FacesMessage.SEVERITY_INFO, langBean.msg("commons.message.state.success"), langBean.msg("create.team.success"));
            } catch (TeamAlreadyExistException e) {
                log.error("Team already exists.", e);
                displayMessage(FacesMessage.SEVERITY_ERROR, langBean.msg("commons.message.state.error"), langBean.msg("commons.error.team.alreadyexist", fTeamName));
            } catch (FailedTeamSaveException e) {
                log.error("Failed to save team.", e);
                displayMessage(FacesMessage.SEVERITY_ERROR, langBean.msg("commons.message.state.error"), langBean.msg("commons.error.team.failedsave"));
            }
        }
    }

}
