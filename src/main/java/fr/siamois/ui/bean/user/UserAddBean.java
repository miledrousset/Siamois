package fr.siamois.ui.bean.user;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.UserAlreadyExist;
import fr.siamois.domain.models.exceptions.auth.InvalidEmail;
import fr.siamois.domain.models.exceptions.auth.InvalidPassword;
import fr.siamois.domain.models.exceptions.auth.InvalidUsername;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.PersonService;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import jakarta.faces.application.FacesMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * <p>This bean handles the creation of a new user</p>
 * <p>It is used to create a new user</p>
 *
 * @author Julien Linget
 */
@Slf4j
@Component
@Getter
@Setter
@SessionScoped
public class UserAddBean implements Serializable {

    // Injections
    private final LangBean langBean;
    private final transient PersonService personService;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient InstitutionService institutionService;

    // Fields
    private String fManagerUsername;
    private String fManagerEmail;
    private String fManagerPassword;
    private String fManagerConfirmPassword;

    public UserAddBean(LangBean langBean,
                       PersonService personService,
                       SessionSettingsBean sessionSettingsBean,
                       InstitutionService institutionService) {
        this.langBean = langBean;
        this.personService = personService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.institutionService = institutionService;
    }

    /**
     * Reset all the variables
     */
    public void resetVariables() {
        fManagerUsername = "";
        fManagerEmail = "";
        fManagerPassword = "";
        fManagerConfirmPassword = "";
    }

    /**
     * Create a new user with the given information in the database
     *
     * @return the created user or null if an error occurred
     */
    public Person createUser(boolean isManager) {

        if (!fManagerPassword.equals(fManagerConfirmPassword)) {
            displayErrorMessage(langBean.msg("commons.error.password.nomatch"));
            return null;
        }

        try {
            Person toSave = new Person();
            toSave.setUsername(fManagerUsername);
            toSave.setPassword(fManagerPassword);
            toSave.setMail(fManagerEmail);

            Person person = personService.createPerson(toSave);

            if (isManager) {
                institutionService.addToManagers(sessionSettingsBean.getSelectedInstitution(), person);
            }

            MessageUtils.displayMessage(FacesMessage.SEVERITY_INFO, langBean.msg("commons.message.state.success"), langBean.msg("create.team.manager.created"));

            resetVariables();
            return person;
        } catch (UserAlreadyExist e) {
            log.error("Username already exists.");
            displayErrorMessage(langBean.msg("commons.error.user.alreadyexist", fManagerUsername));
        } catch (InvalidUsername e) {
            log.error("Invalid username.");
            displayErrorMessage(langBean.msg("commons.error.user.username.invalid"));
        } catch (InvalidEmail e) {
            log.error("Invalid email.");
            displayErrorMessage(langBean.msg("commons.error.user.email.invalid"));
        } catch (InvalidPassword e) {
            log.error("Invalid password.");
            displayErrorMessage(langBean.msg("commons.error.user.password.invalid"));
        }

        return null;
    }

    private void displayErrorMessage(String message) {
        MessageUtils.displayMessage(FacesMessage.SEVERITY_ERROR, langBean.msg("commons.message.state.error"), message);
    }

}
