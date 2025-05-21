package fr.siamois.domain.utils;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.exceptions.api.NotSiamoisThesaurusException;
import fr.siamois.domain.models.vocabulary.GlobalFieldConfig;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.ui.bean.LangBean;
import jakarta.faces.application.FacesMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static fr.siamois.domain.utils.MessageUtils.*;

public class FieldConfigUtils {

    private FieldConfigUtils() {}

    public static void saveConfig(UserInfo info,
                                  VocabularyService vocabularyService,
                                  FieldConfigurationService fieldConfigurationService,
                                  LangBean langBean,
                                  String uri,
                                  boolean isForInstitution) {
        if (StringUtils.isEmpty(uri)) return;

        try {

            Vocabulary dbVocabulary = vocabularyService.findOrCreateVocabularyOfUri(uri);
            Optional<GlobalFieldConfig> wrongConfig;

            if (isForInstitution) {
                wrongConfig = fieldConfigurationService.setupFieldConfigurationForInstitution(info, dbVocabulary);
            } else {
                wrongConfig = fieldConfigurationService.setupFieldConfigurationForUser(info, dbVocabulary);
            }

            if (wrongConfig.isEmpty()) {
                displayMessage(langBean, FacesMessage.SEVERITY_INFO, "myProfile.message.success");
                return;
            }

            if (!wrongConfig.get().missingFieldCode().isEmpty())
                missingFieldCodes(langBean, wrongConfig.get());
            displayErrorMessage(langBean, "common.error.thesaurusConfig.notfound");


        } catch (NotSiamoisThesaurusException | InvalidEndpointException e) {
            displayMessage(langBean, FacesMessage.SEVERITY_ERROR, "common.error.thesaurusConfig.wrong");
        }
    }

    private static void missingFieldCodes(LangBean langBean, GlobalFieldConfig wrongConfig) {
        displayMessage(langBean,
                FacesMessage.SEVERITY_ERROR,
                "common.error.thesaurusConfig.missingCodes",
                wrongConfig.missingFieldCode());
    }

}
