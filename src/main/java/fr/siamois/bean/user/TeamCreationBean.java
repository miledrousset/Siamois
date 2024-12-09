package fr.siamois.bean.user;

import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.TeamAlreadyExistException;
import fr.siamois.models.exceptions.UserAlreadyExist;
import fr.siamois.models.exceptions.field.InvalidUserInformation;
import fr.siamois.services.PersonService;
import fr.siamois.services.TeamService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.event.ValueChangeEvent;
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

    public TeamCreationBean(TeamService teamService, PersonService personService) {
        this.teamService = teamService;
        this.personService = personService;
    }

    public void init() {
        loadManagers();
    }

    public void loadManagers() {
        managers = teamService.findAllManagers();
    }

    private void displayMessage(FacesMessage.Severity severity, String message) {
        FacesMessage facesMessage = new FacesMessage(severity, message, null);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    public void saveTeamAndManager() {
        if (fManagerSelectionType.equalsIgnoreCase("create")) {

            if (!fManagerPassword.equals(fManagerConfirmPassword)) {
                displayMessage(FacesMessage.SEVERITY_ERROR, "Passwords do not match.");
                return;
            }

            try {
                fManager = personService.createPerson(fManagerUsername, fManagerEmail, fManagerPassword);
                fManager = personService.addPersonToTeamManagers(fManager);
            } catch (InvalidUserInformation e) {
                log.error("Invalid user information.", e);
                displayMessage(FacesMessage.SEVERITY_ERROR, "Invalid user information.");
                return;
            } catch (UserAlreadyExist e) {
                log.error("User already exist.", e);
                displayMessage(FacesMessage.SEVERITY_ERROR, "Username " + fManagerUsername + " already exists.");
                return;
            }
        }

        if (fManager != null)  {
            try {
                teamService.createTeam(fTeamName, fDescription, fManager);
                displayMessage(FacesMessage.SEVERITY_INFO, "Team created successfully.");
            } catch (TeamAlreadyExistException e) {
                log.error("Team already exists.", e);
                displayMessage(FacesMessage.SEVERITY_ERROR, "Team with the given name already exists.");
            }
        }
    }

}
