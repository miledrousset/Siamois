package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.exceptions.api.NotSiamoisThesaurusException;
import fr.siamois.domain.models.exceptions.auth.*;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@Getter
@Setter
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class SettingsBean {

    private final SessionSettingsBean sessionSettingsBean;
    private final PersonService personService;
    private final FieldConfigurationService fieldConfigurationService;
    private final VocabularyService vocabularyService;

    private String fEmail;
    private String fLastname;
    private String fFirstname;

    private String fThesaurusUrl;

    public SettingsBean(SessionSettingsBean sessionSettingsBean, PersonService personService, FieldConfigurationService fieldConfigurationService, VocabularyService vocabularyService) {
        this.sessionSettingsBean = sessionSettingsBean;
        this.personService = personService;
        this.fieldConfigurationService = fieldConfigurationService;
        this.vocabularyService = vocabularyService;
    }

    public void init() {
        UserInfo info = sessionSettingsBean.getUserInfo();
        Person user = info.getUser();
        fEmail = user.getMail();
        fLastname = user.getLastname();
        fFirstname = user.getName();

        try {
            Concept result = fieldConfigurationService.findConfigurationForFieldCode(info, SpatialUnit.CATEGORY_FIELD_CODE);
            fThesaurusUrl = result.getVocabulary().getUri();
        } catch (NoConfigForFieldException e) {
            log.debug("No config", e);
        }

    }

    public void saveProfile() {
        Person user = sessionSettingsBean.getAuthenticatedUser();
        if (fEmail != null && !fEmail.isEmpty() && !fEmail.equals(user.getMail())) {
            user.setMail(fEmail);
        }

        if (fLastname != null && !fLastname.isEmpty() && !fLastname.equals(user.getLastname())) {
            user.setLastname(fLastname);
        }

        if (fFirstname != null && !fFirstname.isEmpty() && !fFirstname.equals(user.getName())) {
            user.setName(fFirstname);
        }

        try {
            personService.updatePerson(user);
        } catch (InvalidNameException e) {
            log.error(e.getMessage());
        } catch (InvalidUserInformationException | UserAlreadyExistException e) {
            log.error("There was a problem while updating the person", e);
        }
    }

    public void saveThesaurusUserConfig() {
        if (fThesaurusUrl != null && !fThesaurusUrl.isEmpty()) {
            UserInfo info = sessionSettingsBean.getUserInfo();
            try {
                Vocabulary vocabulary = vocabularyService.findVocabularyOfUri(info, fThesaurusUrl);
                fieldConfigurationService.setupFieldConfigurationForUser(info, vocabulary);
                log.debug("Thesaurus configuration updated");
            } catch (InvalidEndpointException e) {
                log.error("Invalid endpoint", e);
            } catch (NotSiamoisThesaurusException e) {
                log.error("Not siamois thesaurus", e);
            }
        }
    }

}
