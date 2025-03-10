package fr.siamois.ui.bean;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.*;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.utils.MessageUtils;
import jakarta.faces.application.FacesMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Data
public class UserSettingsBean {

    private final SessionSettingsBean sessionSettingsBean;
    private final PersonService personService;
    private final LangBean langBean;

    private String firstName;
    private String lastName;
    private String email;

    public UserSettingsBean(SessionSettingsBean sessionSettingsBean, PersonService personService, LangBean langBean) {
        this.sessionSettingsBean = sessionSettingsBean;
        this.personService = personService;
        this.langBean = langBean;
    }

    public void init() {
        Person user = sessionSettingsBean.getUserInfo().getUser();
        firstName = user.getName();
        lastName = user.getLastname();
        email = user.getMail();
    }

    public String getUsername() {
        return sessionSettingsBean.getUserInfo().getUser().getUsername();
    }

    public int getNameMaxLength() {
        return Person.NAME_MAX_LENGTH;
    }

    public int getEmailMaxLength() {
        return Person.MAIL_MAX_LENGTH;
    }

    public void updateSettings() {
        Person person = sessionSettingsBean.getUserInfo().getUser();
        boolean hasChanged = false;
        if (fieldHasChanged(firstName, person.getName())) {
            person.setName(firstName);
            hasChanged = true;
        }
        if (fieldHasChanged(lastName, person.getLastname())) {
            person.setLastname(lastName);
            hasChanged = true;
        }
        if (fieldHasChanged(email, person.getMail())) {
            person.setMail(email);
            hasChanged = true;
        }

        if (!hasChanged) {
            MessageUtils.displayMessage(FacesMessage.SEVERITY_INFO, "Info", "No changes made");
            return;
        }

        try {
            personService.updatePerson(person);
            MessageUtils.displayMessage(FacesMessage.SEVERITY_INFO, "Info", "Person updated successfully");
        } catch (UserAlreadyExistException e) {
            MessageUtils.displayErrorMessage(langBean, "Username already exist.");
        } catch (InvalidNameException e) {
            MessageUtils.displayErrorMessage(langBean, "Invalid name.");
        } catch (InvalidPasswordException e) {
            MessageUtils.displayErrorMessage(langBean, "Invalid password.");
        } catch (InvalidUsernameException e) {
            MessageUtils.displayErrorMessage(langBean, "Invalid username.");
        } catch (InvalidEmailException e) {
            MessageUtils.displayErrorMessage(langBean, "Invalid email.");
        }

    }

    private boolean fieldHasChanged(String field, String personField) {
        return !StringUtils.isEmpty(field) && !field.equals(personField);
    }

}
