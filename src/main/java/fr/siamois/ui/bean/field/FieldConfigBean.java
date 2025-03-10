package fr.siamois.ui.bean.field;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.events.InstitutionChangeEvent;
import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.exceptions.api.NotSiamoisThesaurusException;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.GlobalFieldConfig;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.domain.utils.FieldConfigUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.converter.VocabularyConverter;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.IOException;
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
    private String fUri;

    public FieldConfigBean(VocabularyService vocabularyService,
                           LangBean langBean,
                           FieldConfigurationService fieldConfigurationService,
                           SessionSettingsBean sessionSettingsBean,
                           VocabularyConverter vocabularyConverter) {
        this.vocabularyService = vocabularyService;
        this.langBean = langBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.vocabularyConverter = vocabularyConverter;
    }

    public void resetVariables() {
        vocabularies = null;
        userVocab = null;
    }

    public void onLoad() {
        log.trace("On Load called");
        UserInfo info = sessionSettingsBean.getUserInfo();
        try {
            Concept config = fieldConfigurationService.findConfigurationForFieldCode(info, SpatialUnit.CATEGORY_FIELD_CODE);

            String uri = String.format("%s/?idt=%s",
                    config.getVocabulary().getBaseUri(),
                    config.getVocabulary().getExternalVocabularyId());

            fUri = uri;

        } catch (NoConfigForFieldException e) {
            log.trace("No config set for user {} of institution {}",
                    info.getUser().getUsername(),
                    info.getInstitution().getName());
        }
    }

    public void saveInstitutionConfig() {
        UserInfo info = sessionSettingsBean.getUserInfo();
        FieldConfigUtils.saveConfig(
                info,
                vocabularyService,
                fieldConfigurationService,
                langBean,
                fUri,
                true
        );
    }


    @EventListener(InstitutionChangeEvent.class)
    public void onInstitutionChange() {
        resetVariables();
    }
}
