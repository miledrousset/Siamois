package fr.siamois.ui.bean.settings;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.events.InstitutionChangeEvent;
import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.exceptions.api.NotSiamoisThesaurusException;
import fr.siamois.domain.models.exceptions.auth.InvalidNameException;
import fr.siamois.domain.models.exceptions.auth.InvalidUserInformationException;
import fr.siamois.domain.models.exceptions.auth.UserAlreadyExistException;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.settings.PersonSettings;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.InstitutionService;
import fr.siamois.domain.services.LangService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.publisher.InstitutionChangeEventPublisher;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class ProfileSettingsBean implements Serializable {

    private final SessionSettingsBean sessionSettingsBean;
    private final transient PersonService personService;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient VocabularyService vocabularyService;
    private final transient InstitutionService institutionService;
    private final transient InstitutionChangeEventPublisher institutionChangeEventPublisher;
    private final transient LangService langService;
    private final LangBean langBean;

    private List<Institution> refInstitutions;
    private PersonSettings personSettings;

    private String fEmail;
    private String fLastname;
    private String fFirstname;

    private String fSelectedLang;

    private String fThesaurusUrl;
    private Institution fDefaultInstitution;

    public ProfileSettingsBean(SessionSettingsBean sessionSettingsBean,
                               PersonService personService,
                               FieldConfigurationService fieldConfigurationService,
                               VocabularyService vocabularyService,
                               InstitutionService institutionService,
                               InstitutionChangeEventPublisher institutionChangeEventPublisher,
                               LangService langService,
                               LangBean langBean) {
        this.sessionSettingsBean = sessionSettingsBean;
        this.personService = personService;
        this.fieldConfigurationService = fieldConfigurationService;
        this.vocabularyService = vocabularyService;
        this.institutionService = institutionService;
        this.institutionChangeEventPublisher = institutionChangeEventPublisher;
        this.langService = langService;
        this.langBean = langBean;
    }

    @EventListener(InstitutionChangeEvent.class)
    public void init() {
        if (fDefaultInstitution == null) {
            UserInfo info = sessionSettingsBean.getUserInfo();
            Person user = info.getUser();
            initPersonSection(info);
            initThesaurusSection(info);
            initInstitutions(user, info);

            fSelectedLang = langBean.getLanguageCode();
        }
    }

    private void initInstitutions(Person user, UserInfo info) {
        refInstitutions = institutionService.findInstitutionsOfPerson(user);
        fDefaultInstitution = refInstitutions.stream()
                .filter(institution ->
                        institution.getId().equals(info.getInstitution().getId()))
                .findFirst()
                .orElse(refInstitutions.get(0));
        log.trace("Found {} institutions", refInstitutions.size());
    }

    private void initThesaurusSection(UserInfo info) {
        try {
            Concept result = fieldConfigurationService.findConfigurationForFieldCode(info, SpatialUnit.CATEGORY_FIELD_CODE);
            fThesaurusUrl = result.getVocabulary().getUri();
        } catch (NoConfigForFieldException e) {
            log.debug("User has no thesaurus configuration for fieldCode {}", SpatialUnit.CATEGORY_FIELD_CODE);
        }
    }

    private void initPersonSection(UserInfo info) {
        Person user = info.getUser();
        fEmail = user.getMail();
        fLastname = user.getLastname();
        fFirstname = user.getName();
        personSettings = personService.createOrGetSettingsOf(user);
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
                Vocabulary vocabulary = vocabularyService.findOrCreateVocabularyOfUri(fThesaurusUrl);
                fieldConfigurationService.setupFieldConfigurationForUser(info, vocabulary);
                log.debug("Thesaurus configuration updated");
            } catch (InvalidEndpointException e) {
                log.error("Invalid endpoint", e);
            } catch (NotSiamoisThesaurusException e) {
                log.error("Not siamois thesaurus", e);
            }
        }
    }

    public List<Locale> getRefLangs() {
        List<String> langCodes = langService.getAvailableLanguages();
        return langCodes.stream()
                .map(Locale::new)
                .toList();
    }

    public String localeToLangName(Locale locale) {
        return StringUtils.capitalize(locale.getDisplayName(locale));
    }

    public String localeToLangCode(Locale locale) {
        return locale.getLanguage();
    }

    public String codeToLangName(String code) {
        return localeToLangName(new Locale(code));
    }

    public void savePreferences() {
        boolean reloadPage = false;
        log.trace("Save preferences called");
        personSettings.setDefaultInstitution(fDefaultInstitution);
        log.trace("New default institution: {}", fDefaultInstitution);
        log.trace("Language is {}. Existing is {}", fSelectedLang, personSettings.getLangCode());
        if (!fSelectedLang.equalsIgnoreCase(personSettings.getLangCode())) {
            personSettings.setLangCode(fSelectedLang);
            log.trace("Selected language code: {}", fSelectedLang);
            reloadPage = true;
        } else {
            log.trace("No new language code selected");
        }

        personSettings = personService.updatePersonSettings(personSettings);
        log.trace("Settings updated");


        if (reloadPage) {
            PrimeFaces.current().executeScript("location.reload();");
        }
    }

}
