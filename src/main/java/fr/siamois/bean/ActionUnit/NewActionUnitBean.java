package fr.siamois.bean.ActionUnit;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.vocabulary.FieldConfigurationWrapper;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;


@Slf4j
@Data
@Component
@SessionScoped
public class NewActionUnitBean implements Serializable {

    // Deps
    private final ActionUnitService actionUnitService;
    private final FieldConfigurationService fieldConfigurationService;
    private final FieldService fieldService;
    private final LangBean langBean;
    private final SessionSettings sessionSettings;

    // Local
    private ActionUnit actionUnit;
    private List<ConceptFieldDTO> concepts;
    private ConceptFieldDTO fieldType = null;
    private FieldConfigurationWrapper configurationWrapper;


    public NewActionUnitBean(ActionUnitService actionUnitService, FieldConfigurationService fieldConfigurationService, FieldService fieldService, LangBean langBean, SessionSettings sessionSettings) {
        this.actionUnitService = actionUnitService;
        this.fieldConfigurationService = fieldConfigurationService;
        this.fieldService = fieldService;
        this.langBean = langBean;
        this.sessionSettings = sessionSettings;
    }


    public String save() {
        try {
            Person author = sessionSettings.getAuthenticatedUser();
            actionUnit.setAuthor(author);
            actionUnit.setBeginDate(OffsetDateTime.now()); // todo : implement
            actionUnit.setEndDate(OffsetDateTime.now());  // todo : implement
            Vocabulary vocabulary = configurationWrapper.vocabularyConfig();
            if (vocabulary == null) vocabulary = configurationWrapper.vocabularyCollectionsConfig().get(0).getVocabulary();

            this.actionUnit = actionUnitService.save(actionUnit, vocabulary, fieldType);

//            // Display message
//            FacesContext.getCurrentInstance().addMessage(null,
//                    new FacesMessage(
//                            FacesMessage.SEVERITY_INFO,
//                            "Info",
//                            langBean.msg("actionunit.created", this.actionUnit.getName())));
//
//            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            return "/pages/actionUnit/actionUnit?faces-redirect=true&id=" + this.actionUnit.getId().toString();

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("actionunit.creationfailed", this.actionUnit.getName())));

            log.error("Error while saving: " + e.getMessage());
            // todo : add error message
            return null;
        }
    }

    /**
     * Fetch the autocomplete results on API for the type field and add them to the list of concepts.
     *
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<ConceptFieldDTO> completeActionUnitType(String input) {

        Person person = AuthenticatedUserUtils.getAuthenticatedUser().orElseThrow(() -> new IllegalStateException("User should be connected"));

        try {
            if(this.configurationWrapper == null) {
                this.configurationWrapper = fieldConfigurationService.fetchConfigurationOfFieldCode(person, ActionUnit.TYPE_FIELD_CODE);
            }
        } catch (NoConfigForField e) {
            log.error("No collection for field " + ActionUnit.TYPE_FIELD_CODE);
        }

        concepts = fieldService.fetchAutocomplete(configurationWrapper, input, langBean.getLanguageCode());
        return concepts;
    }

    @PostConstruct
    public void init() {
        fieldType = null;
        actionUnit = new ActionUnit();
        actionUnit.setName("Nouvelle action");
    }

    public void reinit(SpatialUnit spatialUnit) {
        this.actionUnit.setSpatialUnit(spatialUnit);
    }


}
