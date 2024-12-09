package fr.siamois.bean.user;

import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.UserAlreadyExist;
import fr.siamois.models.exceptions.field.InvalidUserInformation;
import fr.siamois.services.PersonService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
@Component
public class UserCreationBean implements Serializable {
    private final PersonService personService;

    // Injections

    // Storage
    List<Team> refTeams = new ArrayList<>();
    List<Team> filteredTeams = new ArrayList<>();

    // Fields
    private String vUsername;
    private String vPassword;
    private String vEmail;
    private String vConfirmPassword;
    private List<Team> vTeams = new ArrayList<>();

    public UserCreationBean(PersonService personService) {
        this.personService = personService;
    }

    public void init() {
        resetVariables();
        refTeams = personService.findAllTeams();
    }

    private void resetVariables() {
        refTeams = new ArrayList<>();
        filteredTeams = new ArrayList<>();
        vUsername = "";
        vPassword = "";
        vEmail = "";
        vConfirmPassword = "";
        vTeams = new ArrayList<>();
    }

    public void createUser() {

        if (!vPassword.equals(vConfirmPassword)) {
            displayErrorMessage("Passwords do not match.");
            return;
        }

        try {
            Person person = personService.createPerson(vUsername, vEmail, vPassword);
            person = personService.addPersonToTeamManagers(person);
            personService.addPersonToTeam(person, vTeams.toArray(new Team[0]));
            displayMessage(FacesMessage.SEVERITY_INFO, "Success", "User created successfully.");
        } catch (InvalidUserInformation e) {
            log.error("Invalid user information.", e);
            displayErrorMessage(e.getUserMessage());
        } catch (UserAlreadyExist e) {
            log.error("Username already exists.", e);
            displayErrorMessage("Username already exists.");
        }
    }

    private void displayMessage(FacesMessage.Severity severity, String title, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, message));
    }
    private void displayErrorMessage(String message) {
        displayMessage(FacesMessage.SEVERITY_ERROR, "Error", message);
    }

}
