package fr.siamois.bean.user;

import fr.siamois.bean.LangBean;
import fr.siamois.models.auth.Person;
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

import javax.faces.bean.SessionScoped;

@Slf4j
@Component
@Getter
@Setter
@SessionScoped
public class UserAddBean {

    // Injections
    private final LangBean langBean;
    private final PersonService personService;

    // Fields
    private String fManagerUsername;
    private String fManagerEmail;
    private String fManagerPassword;
    private String fManagerConfirmPassword;

    public UserAddBean(LangBean langBean, PersonService personService) {
        this.langBean = langBean;
        this.personService = personService;
    }

    public void resetVariables() {
        fManagerUsername = "";
        fManagerEmail = "";
        fManagerPassword = "";
        fManagerConfirmPassword = "";
    }

    public Person createUser() {

        if (!fManagerPassword.equals(fManagerConfirmPassword)) {
            displayErrorMessage(langBean.msg("commons.error.password.nomatch"));
            return null;
        }

        try {
            Person person = personService.createPerson(fManagerUsername, fManagerEmail, fManagerPassword);
            displayMessage(FacesMessage.SEVERITY_INFO, langBean.msg("commons.message.state.success"), langBean.msg("create.team.manager.created"));
            resetVariables();
            return person;
        } catch (UserAlreadyExist e) {
            log.error("Username already exists.", e);
            displayErrorMessage(langBean.msg("commons.error.user.alreadyexist", fManagerUsername));
        } catch (InvalidUsername e) {
            log.error("Invalid username.", e);
            displayErrorMessage(langBean.msg("commons.error.user.username.invalid"));
        } catch (InvalidEmail e) {
            log.error("Invalid email.", e);
            displayErrorMessage(langBean.msg("commons.error.user.email.invalid"));
        } catch (InvalidPassword e) {
            log.error("Invalid password.", e);
            displayErrorMessage(langBean.msg("commons.error.user.password.invalid"));
        }

        return null;
    }

    private void displayMessage(FacesMessage.Severity severity, String title, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, message));
    }
    private void displayErrorMessage(String message) {
        displayMessage(FacesMessage.SEVERITY_ERROR, langBean.msg("commons.message.state.error"), message);
    }

}
