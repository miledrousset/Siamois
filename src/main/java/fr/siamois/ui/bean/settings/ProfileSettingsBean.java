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
import fr.siamois.domain.services.publisher.LangageChangeEventPublisher;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import jakarta.faces.application.FacesMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private final transient LangService langService;
    private final LangBean langBean;
    private final transient LangageChangeEventPublisher langageChangeEventPublisher;

    private List<Institution> refInstitutions;
    private PersonSettings personSettings;
    private Concept refConfigConcept;

    private String fEmail;
    private String fLastname;
    private String fFirstname;

    private String fSelectedLang;

    private String fThesaurusUrl;
    private Long fDefaultInstitutionId;

    public ProfileSettingsBean(SessionSettingsBean sessionSettingsBean,
                               PersonService personService,
                               FieldConfigurationService fieldConfigurationService,
                               VocabularyService vocabularyService,
                               InstitutionService institutionService,
                               LangService langService,
                               LangBean langBean,
                               LangageChangeEventPublisher langageChangeEventPublisher) {
        this.sessionSettingsBean = sessionSettingsBean;
        this.personService = personService;
        this.fieldConfigurationService = fieldConfigurationService;
        this.vocabularyService = vocabularyService;
        this.institutionService = institutionService;
        this.langService = langService;
        this.langBean = langBean;
        this.langageChangeEventPublisher = langageChangeEventPublisher;
    }

    @EventListener(InstitutionChangeEvent.class)
    public void init() {
        UserInfo info = sessionSettingsBean.getUserInfo();
        Person user = info.getUser();
        initPersonSection(info);
        initThesaurusSection(info);
        initInstitutions(user, info);

        fSelectedLang = langBean.getLanguageCode();
    }

    private void initInstitutions(Person user, UserInfo info) {
        refInstitutions = institutionService.findInstitutionsOfPerson(user);
        PersonSettings settings = personService.createOrGetSettingsOf(user);
        if (settings.getDefaultInstitution() != null) {
            fDefaultInstitutionId = settings.getDefaultInstitution().getId();
        } else {
            fDefaultInstitutionId = info.getInstitution().getId();
        }
        log.trace("Found {} institutions", refInstitutions.size());
    }

    private void initThesaurusSection(UserInfo info) {
        try {
            refConfigConcept = fieldConfigurationService.findConfigurationForFieldCode(info, SpatialUnit.CATEGORY_FIELD_CODE);
            fThesaurusUrl = refConfigConcept.getVocabulary().getUri();
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
        boolean updated = false;
        Person user = sessionSettingsBean.getAuthenticatedUser();
        if (fEmail != null && !fEmail.isEmpty() && !fEmail.equals(user.getMail())) {
            user.setMail(fEmail);
            updated = true;
        }

        if (fLastname != null && !fLastname.isEmpty() && !fLastname.equals(user.getLastname())) {
            user.setLastname(fLastname);
            updated = true;
        }

        if (fFirstname != null && !fFirstname.isEmpty() && !fFirstname.equals(user.getName())) {
            user.setName(fFirstname);
            updated = true;
        }

        if (!updated) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_WARN, "Aucune valeur n'a été changée.");
            return;
        }

        try {
            personService.updatePerson(user);
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_INFO, "Informations modifiés avec succès");
        } catch (InvalidNameException e) {
            log.error(e.getMessage());
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (InvalidUserInformationException | UserAlreadyExistException e) {
            log.error("There was a problem while updating the person", e);
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Erreur interne");
        }
    }

    public void saveThesaurusUserConfig() {
        if (fThesaurusUrl == null || fThesaurusUrl.isEmpty()) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_WARN, "Aucun thésaurus n'a été renseigné.");
            return;
        }

        if (refConfigConcept != null && refConfigConcept.getVocabulary().getUri().equals(fThesaurusUrl)) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_WARN, "La configuration du thésaurus n'a pas été modifiée.");
            return;
        }

        UserInfo info = sessionSettingsBean.getUserInfo();
        try {
            Vocabulary vocabulary = vocabularyService.findOrCreateVocabularyOfUri(fThesaurusUrl);
            fieldConfigurationService.setupFieldConfigurationForUser(info, vocabulary);
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_INFO, "La configuration a bien été enregistrée");
        } catch (InvalidEndpointException e) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "L'URI renseignée n'est pas valide. Vérifiez l'URI entrée.");
        } catch (NotSiamoisThesaurusException e) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Le thésaurus sélectionné ne dispose pas des notations d'un thésaurus SIAMOIS. Vérifiez les notations des concepts.");
        }
    }

    public List<Locale> getRefLangs() {
        List<String> langCodes = langService.getAvailableLanguages();
        return langCodes.stream()
                .map(Locale::new)
                .toList();
    }

    public String localeToLangName(Locale locale) {
        if (locale == null) {
            return "NULL";
        }
        return StringUtils.capitalize(locale.getDisplayName(locale));
    }

    public String localeToLangCode(Locale locale) {
        if (locale == null) {
            return "NULL";
        }
        return locale.getLanguage();
    }

    public String codeToLangName(String code) {
        return localeToLangName(new Locale(code));
    }

    private Institution findInstitutionById(Long id) {
        return refInstitutions.stream()
                .filter(institution -> institution.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Institution not found"));
    }

    public void savePreferences() {
        personSettings.setDefaultInstitution(findInstitutionById(fDefaultInstitutionId));
        if (!fSelectedLang.equalsIgnoreCase(personSettings.getLangCode())) {
            personSettings.setLangCode(fSelectedLang);
        }

        personSettings = personService.updatePersonSettings(personSettings);
        langageChangeEventPublisher.publishInstitutionChangeEvent();

        MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_INFO, "Les préférences ont bien été sauvegardées.");
    }

    public String labelOfInstitutionWithId(Long id) {
        return findInstitutionById(id).getName();
    }


}
