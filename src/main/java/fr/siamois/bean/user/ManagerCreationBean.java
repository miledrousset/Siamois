package fr.siamois.bean.user;

import fr.siamois.bean.LangBean;
import fr.siamois.models.Team;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.FailedTeamSaveException;
import fr.siamois.models.exceptions.UserAlreadyExist;
import fr.siamois.models.exceptions.auth.InvalidEmail;
import fr.siamois.models.exceptions.auth.InvalidPassword;
import fr.siamois.models.exceptions.auth.InvalidUsername;
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
public class ManagerCreationBean implements Serializable {
    private final PersonService personService;
    private final LangBean langBean;

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

    public ManagerCreationBean(PersonService personService, LangBean langBean) {
        this.personService = personService;
        this.langBean = langBean;
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
            displayErrorMessage(langBean.msg("commons.error.password.nomatch"));
            return;
        }

        try {
            Person person = personService.createPerson(vUsername, vEmail, vPassword);
            person = personService.addPersonToTeamManagers(person);
            personService.addPersonToTeam(person, vTeams.toArray(new Team[0]));
            displayMessage(FacesMessage.SEVERITY_INFO, langBean.msg("commons.message.state.success"), langBean.msg("create.team.manager.created"));
        } catch (UserAlreadyExist e) {
            log.error("Username already exists.", e);
            displayErrorMessage(langBean.msg("commons.error.user.alreadyexist", vUsername));
        } catch (InvalidUsername e) {
            log.error("Invalid username.", e);
            displayErrorMessage(langBean.msg("commons.error.user.username.invalid"));
        } catch (InvalidEmail e) {
            log.error("Invalid email.", e);
            displayErrorMessage(langBean.msg("commons.error.user.email.invalid"));
        } catch (InvalidPassword e) {
            log.error("Invalid password.", e);
            displayErrorMessage(langBean.msg("commons.error.user.password.invalid"));
        } catch (FailedTeamSaveException e) {
            log.error("Failed to save team.", e);
            displayErrorMessage(langBean.msg("commons.error.team.save"));
        }
    }

    private void displayMessage(FacesMessage.Severity severity, String title, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, message));
    }
    private void displayErrorMessage(String message) {
        displayMessage(FacesMessage.SEVERITY_ERROR, langBean.msg("commons.message.state.error"), message);
    }

}
