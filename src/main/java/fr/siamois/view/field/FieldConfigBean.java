package fr.siamois.view.field;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.events.InstitutionChangeEvent;
import fr.siamois.domain.models.exceptions.NoConfigForField;
import fr.siamois.domain.models.exceptions.NotSiamoisThesaurusException;
import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.GlobalFieldConfig;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.view.LangBean;
import fr.siamois.view.SessionSettingsBean;
import fr.siamois.view.converter.VocabularyConverter;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class FieldConfigBean implements Serializable {

    // Injections
    private final transient VocabularyService vocabularyService;
    private final LangBean langBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final SessionSettingsBean sessionSettingsBean;
    private final VocabularyConverter vocabularyConverter;

    // Storage
    private transient List<Vocabulary> vocabularies;
    private transient List<Vocabulary> userVocab;

    // Fields
    private String fInstance;
    private Vocabulary fSelectedVocab;

    private String fUserInstance;
    private Vocabulary fUserSelectedVocab;

    public FieldConfigBean(VocabularyService vocabularyService, LangBean langBean, FieldConfigurationService fieldConfigurationService, SessionSettingsBean sessionSettingsBean, VocabularyConverter vocabularyConverter) {
        this.vocabularyService = vocabularyService;
        this.langBean = langBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.vocabularyConverter = vocabularyConverter;
    }

    public void resetVariables() {
        vocabularies = null;
        userVocab = null;
        fInstance = null;
        fSelectedVocab = null;
        fUserInstance = null;
        fUserSelectedVocab = null;
    }

    public void onLoad() {
        log.trace("On Load called");
        UserInfo info = sessionSettingsBean.getUserInfo();
        try {
            Concept config = fieldConfigurationService.findConfigurationForFieldCode(info, SpatialUnit.CATEGORY_FIELD_CODE);
            fInstance = config.getVocabulary().getBaseUri();
            fSelectedVocab = config.getVocabulary();
        } catch (NoConfigForField e) {
            log.trace("No config set for user {} of institution {}",
                    info.getUser().getUsername(),
                    info.getInstitution().getName());
        }
    }

    public void loadInstance() {
        if (fInstance.endsWith("/"))
            fInstance = fInstance.substring(0, fInstance.length() - 1);
        try {
            vocabularies = vocabularyService.findAllPublicThesaurus(fInstance, langBean.getLanguageCode());
            if (!vocabularies.isEmpty()) fSelectedVocab = vocabularies.get(0);
        } catch (InvalidEndpointException e) {
            displayErrorMessage("URL is invalid");
        }
    }

    private static void displayErrorMessage(String message) {
        displayMessage(FacesMessage.SEVERITY_ERROR, "Error", message);
    }

    private static void displayMessage(FacesMessage.Severity severity, String title, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, title, message));
    }

    public void loadConfig() {
        Vocabulary databaseVocab = vocabularyService.saveOrGetVocabulary(fSelectedVocab);
        UserInfo info = sessionSettingsBean.getUserInfo();
        saveConfiguration(info, databaseVocab);
    }

    private static void missingFieldCodes(GlobalFieldConfig wrongConfig) {
        log.error("Error in config. Missing codes : {}", wrongConfig.missingFieldCode());
        displayErrorMessage(String.format("The codes %s are not in the thesaurus", wrongConfig.missingFieldCode()));
    }

    public void loadUserInstance() {
        if (fUserInstance.endsWith("/"))
            fUserInstance = fUserInstance.substring(0, fUserInstance.length() - 1);
        try {
            userVocab = vocabularyService.findAllPublicThesaurus(fUserInstance, langBean.getLanguageCode());
            if (!userVocab.isEmpty()) fUserSelectedVocab = userVocab.get(0);
        } catch (InvalidEndpointException | RuntimeException e) {
            log.error("Error while loading instance", e);
            displayErrorMessage(e.getMessage());
        }
    }

    public void loadUserConfig() {
        Vocabulary databaseVocab = vocabularyService.saveOrGetVocabulary(fUserSelectedVocab);

        UserInfo info = sessionSettingsBean.getUserInfo();

        saveConfiguration(info, databaseVocab);
    }

    private void saveConfiguration(UserInfo info, Vocabulary databaseVocab) {
        try {
            Optional<GlobalFieldConfig> config = fieldConfigurationService.setupFieldConfigurationForUser(info, databaseVocab);

            if (config.isEmpty()) {
                displayMessage(FacesMessage.SEVERITY_INFO, "Info", "Configuration saved for USER");
                return;
            }

            GlobalFieldConfig wrongConfig = config.get();

            if (!wrongConfig.missingFieldCode().isEmpty())
                missingFieldCodes(wrongConfig);

            displayErrorMessage("Error in thesaurus config");

        } catch (NotSiamoisThesaurusException e) {
            log.error(e.getMessage());
            displayErrorMessage("Thesaurus is not valid");
        }
    }

    public List<Vocabulary> completeMethod() {
        if (vocabularies == null && !StringUtils.isEmpty(fInstance)) loadInstance();
        return vocabularies;
    }

    @EventListener(InstitutionChangeEvent.class)
    public void onInstitutionChange() {
        resetVariables();
    }
}
