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

    // Storage
    private List<Person> managers;
    private List<Person> filteredManagers;

    // Fields
    private String fTeamName;
    private String fDescription;
    private String fManagerSelectionType;
    private String fTeamDescription;

    private Person fManager;

    private String fManagerUsername;
    private String fManagerEmail;
    private String fManagerPassword;
    private String fManagerConfirmPassword;

    public TeamCreationBean(TeamService teamService, PersonService personService, LangBean langBean) {
        this.teamService = teamService;
        this.personService = personService;
        this.langBean = langBean;
    }

    public void init() {
        loadManagers();
    }

    public void loadManagers() {
        managers = teamService.findAllManagers();
    }

    private void displayMessage(FacesMessage.Severity severity, String title, String message) {
        FacesMessage facesMessage = new FacesMessage(severity, message, message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    public void saveTeamAndManager() {
        if (fManagerSelectionType.equalsIgnoreCase("create")) {

            String errorTitle = langBean.msg("commons.message.state.error");

            if (!fManagerPassword.equals(fManagerConfirmPassword)) {
                displayMessage(FacesMessage.SEVERITY_ERROR, errorTitle, langBean.msg("commons.error.password.nomatch"));
                return;
            }

            try {
                fManager = personService.createPerson(fManagerUsername, fManagerEmail, fManagerPassword);
                fManager = personService.addPersonToTeamManagers(fManager);
            } catch (UserAlreadyExist e) {
                log.error("User already exist.", e);
                displayMessage(FacesMessage.SEVERITY_ERROR, errorTitle,langBean.msg("commons.error.user.alreadyexist", fManagerUsername));
                return;
            } catch (InvalidUsername e) {
                log.error("Invalid username.", e);
                displayMessage(FacesMessage.SEVERITY_ERROR, errorTitle, langBean.msg("commons.error.username.invalid"));
                return;
            } catch (InvalidEmail e) {
                log.error("Invalid email.", e);
                displayMessage(FacesMessage.SEVERITY_ERROR, errorTitle, langBean.msg("commons.error.email.invalid"));
                return;
            } catch (InvalidPassword e) {
                log.error("Invalid password.", e);
                displayMessage(FacesMessage.SEVERITY_ERROR, errorTitle, langBean.msg("commons.error.password.invalid"));
                return;
            }
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
