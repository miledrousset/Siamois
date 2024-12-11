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
    private final UserAddBean userAddBean;

    // Injections

    // Storage
    List<Team> refTeams = new ArrayList<>();
    List<Team> filteredTeams = new ArrayList<>();

    // Fields
    private List<Team> vTeams = new ArrayList<>();

    public ManagerCreationBean(PersonService personService, LangBean langBean, UserAddBean userAddBean) {
        this.personService = personService;
        this.langBean = langBean;
        this.userAddBean = userAddBean;
    }

    public void init() {
        resetVariables();
        refTeams = personService.findAllTeams();
    }

    private void resetVariables() {
        refTeams = new ArrayList<>();
        filteredTeams = new ArrayList<>();
        userAddBean.resetVariables();
        vTeams = new ArrayList<>();
    }

    public void createUser() {
        Person person = userAddBean.createUser();
        personService.addPersonToTeamManagers(person);
    }

    private void displayMessage(FacesMessage.Severity severity, String title, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, message));
    }
    private void displayErrorMessage(String message) {
        displayMessage(FacesMessage.SEVERITY_ERROR, langBean.msg("commons.message.state.error"), message);
    }

}
