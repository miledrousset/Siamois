package fr.siamois.ui.bean;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.auth.*;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.domain.utils.FieldConfigUtils;
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
    private final FieldConfigurationService fieldConfigurationService;
    private final VocabularyService vocabularyService;

    private String firstName;
    private String lastName;
    private String email;

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    private String fUserUri;

    public UserSettingsBean(SessionSettingsBean sessionSettingsBean, PersonService personService, LangBean langBean, FieldConfigurationService fieldConfigurationService, VocabularyService vocabularyService) {
        this.sessionSettingsBean = sessionSettingsBean;
        this.personService = personService;
        this.langBean = langBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.vocabularyService = vocabularyService;
    }

    public void init() throws NoConfigForFieldException {
        log.trace("UserSettingsBean init");
        Person user = sessionSettingsBean.getUserInfo().getUser();
        firstName = user.getName();
        lastName = user.getLastname();
        email = user.getMail();

        UserInfo info = sessionSettingsBean.getUserInfo();

        if (fieldConfigurationService.hasUserConfig(info)) {
            Concept parent = fieldConfigurationService.findConfigurationForFieldCode(info, SpatialUnit.CATEGORY_FIELD_CODE);
            fUserUri = String.format("%s/?idt=%s", parent.getVocabulary().getBaseUri(), parent.getVocabulary().getExternalVocabularyId());
        }

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

    public void changePassword() {
        Person user = sessionSettingsBean.getUserInfo().getUser();
        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(confirmPassword)) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Tous les champs doivent être remplis");
            return;
        }

        if (!personService.passwordMatch(user, oldPassword)) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Ancien mot de passe incorrect");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Les mots de passes ne correspondent pas");
            return;
        }

        try {
            personService.updatePassword(user, newPassword);
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_INFO, "Password changed");
        } catch (InvalidPasswordException e) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, e.getMessage());
        }

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
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_INFO, "Person updated successfully");
        } catch (UserAlreadyExistException e) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Username already exist.");
        } catch (InvalidNameException e) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Invalid name.");
        } catch (InvalidPasswordException e) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Invalid password.");
        } catch (InvalidUsernameException e) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Invalid username.");
        } catch (InvalidEmailException e) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Invalid email.");
        }

    }

    private boolean fieldHasChanged(String field, String personField) {
        return !StringUtils.isEmpty(field) && !field.equals(personField);
    }

    public void saveUserConfig() {
        UserInfo info = sessionSettingsBean.getUserInfo();
        FieldConfigUtils.saveConfig(
                info,
                vocabularyService,
                fieldConfigurationService,
                langBean,
                fUserUri,
                false
        );
    }

}
