package fr.siamois.ui.bean.settings.institution;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.exceptions.api.NotSiamoisThesaurusException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Optional;

@Slf4j
@Component
@Getter
@Setter
@SessionScoped
public class InstitutionThesaurusSettingsBean implements Serializable {

    private final transient FieldConfigurationService fieldConfigurationService;
    private final SessionSettingsBean sessionSettingsBean;
    private final transient VocabularyService vocabularyService;
    private final LangBean langBean;
    private String thesaurusUrl;
    private Institution institution;

    public InstitutionThesaurusSettingsBean(FieldConfigurationService fieldConfigurationService, SessionSettingsBean sessionSettingsBean, VocabularyService vocabularyService, LangBean langBean) {
        this.fieldConfigurationService = fieldConfigurationService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.vocabularyService = vocabularyService;
        this.langBean = langBean;
    }

    public void reset() {
        thesaurusUrl = null;
        institution = null;
    }

    public void init(Institution institution) {
        reset();
        this.institution = institution;
        Optional<String> optVocab = fieldConfigurationService.findVocabularyUrlOfInstitution(institution);
        optVocab.ifPresent(s -> thesaurusUrl = s);

    }

    public void saveConfig() {
        if (StringUtils.isEmpty(thesaurusUrl)) return;

        UserInfo userInfo = sessionSettingsBean.getUserInfo();

        try {
            Vocabulary vocabulary = vocabularyService.findOrCreateVocabularyOfUri(thesaurusUrl);
            fieldConfigurationService.setupFieldConfigurationForInstitution(userInfo, vocabulary);
            MessageUtils.displayInfoMessage(langBean, "myProfile.thesaurus.message.success");
        } catch (InvalidEndpointException e) {
            MessageUtils.displayErrorMessage(langBean, "myProfile.thesaurus.uri.invalid");
        } catch (NotSiamoisThesaurusException e) {
            MessageUtils.displayErrorMessage(langBean, "myProfile.thesaurus.siamois.invalid");
        }


    }

}
