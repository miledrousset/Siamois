package fr.siamois.bean.Field;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.bean.converter.VocabularyConverter;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.UserInfo;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.exceptions.api.InvalidEndpointException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.GlobalFieldConfig;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.VocabularyService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
    private final VocabularyService vocabularyService;
    private final LangBean langBean;
    private final FieldConfigurationService fieldConfigurationService;
    private final SessionSettings sessionSettings;
    private final VocabularyConverter vocabularyConverter;

    // Storage
    private List<Vocabulary> vocabularies;

    // Fields
    private String fInstance;
    private Vocabulary fSelectedVocab;

    public FieldConfigBean(VocabularyService vocabularyService, LangBean langBean, FieldConfigurationService fieldConfigurationService, SessionSettings sessionSettings, VocabularyConverter vocabularyConverter) {
        this.vocabularyService = vocabularyService;
        this.langBean = langBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.sessionSettings = sessionSettings;
        this.vocabularyConverter = vocabularyConverter;
    }

    public void onLoad() {
        log.trace("On Load called");
        UserInfo info = sessionSettings.getUserInfo();
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
        } catch (InvalidEndpointException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "URL is invalid"));
        }
    }

    public void loadConfig() {
        Vocabulary databaseVocab = vocabularyService.saveOrGetVocabulary(fSelectedVocab);

        UserInfo info = new UserInfo(sessionSettings.getSelectedInstitution(), sessionSettings.getAuthenticatedUser());
        Optional<GlobalFieldConfig> config = fieldConfigurationService.setupFieldConfigurationForInstitution(info, databaseVocab);

        if (config.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Configuration saved"));
            return;
        }

        GlobalFieldConfig wrongConfig = config.get();

        if (!wrongConfig.missingFieldCode().isEmpty())
            log.trace("Missing codes : {}", wrongConfig.missingFieldCode());

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error in thesaurus config"));

    }

}
