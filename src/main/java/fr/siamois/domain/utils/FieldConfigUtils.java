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

import static fr.siamois.domain.utils.MessageUtils.displayMessage;
import static fr.siamois.domain.utils.MessageUtils.displayPlainMessage;

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

            Vocabulary dbVocabulary = vocabularyService.findVocabularyOfUri(info, uri);
            Optional<GlobalFieldConfig> wrongConfig;

            if (isForInstitution) {
                wrongConfig = fieldConfigurationService.setupFieldConfigurationForInstitution(info, dbVocabulary);
            } else {
                wrongConfig = fieldConfigurationService.setupFieldConfigurationForUser(info, dbVocabulary);
            }

            if (wrongConfig.isEmpty()) {
                displayMessage(FacesMessage.SEVERITY_INFO, "Info", "Configuration saved");
                return;
            }

            if (!wrongConfig.get().missingFieldCode().isEmpty())
                missingFieldCodes(langBean, wrongConfig.get());
            displayErrorMessage(langBean, "Error in thesaurus config");


        } catch (NotSiamoisThesaurusException | InvalidEndpointException e) {
            displayErrorMessage(langBean, "Thesaurus is not valid");
        }
    }

    private static void missingFieldCodes(LangBean langBean, GlobalFieldConfig wrongConfig) {
        displayErrorMessage(langBean, String.format("The codes %s are not in the thesaurus", wrongConfig.missingFieldCode()));
    }

    private static void displayErrorMessage(LangBean langBean, String message) {
        displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, message);
    }

}
