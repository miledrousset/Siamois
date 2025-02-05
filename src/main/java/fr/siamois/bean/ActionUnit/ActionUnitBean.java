package fr.siamois.bean.ActionUnit;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.actionunit.ActionCode;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.actionunit.ActionUnitService;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.faces.context.FacesContext;

@Slf4j
@Data
@Component
@SessionScoped
public class ActionUnitBean implements Serializable {

    // Deps
    private final ActionUnitService actionUnitService;
    private final LangBean langBean;
    private final SessionSettings sessionSettings;
    private final FieldConfigurationService fieldConfigurationService;
    private final FieldService fieldService;


    // Local
    private ActionUnit actionUnit;
    private String actionUnitErrorMessage;
    private Long id;  // ID of the action unit requested

    // Field related
    private Boolean editType;
    private ConceptFieldDTO fType;

    // todo: remove below and implement properly
    private Concept c1 = new Concept();
    private Concept c2 = new Concept();
    private List<Concept> actionCodeTypeOptions ;
    private List<ActionCode> secondaryActionCodes ;


    public ActionUnitBean(ActionUnitService actionUnitService, LangBean langBean, SessionSettings sessionSettings, FieldConfigurationService fieldConfigurationService, FieldService fieldService) {
        this.actionUnitService = actionUnitService;
        this.langBean = langBean;
        this.sessionSettings = sessionSettings;
        this.fieldConfigurationService = fieldConfigurationService;
        this.fieldService = fieldService;
    }

    @PostConstruct
    public void postConstruct() {
        editType = false;
    }

    /**
     * Fetch the autocomplete results for the action codes
     *
     * @param input the input of the user
     * @return the list of codes the input to display in the autocomplete
     */
    public List<ActionCode> completeActionCode(String input) {

//        codes = fieldService.fetchAutocomplete(configurationWrapper, input, langBean.getLanguageCode());
        ActionCode code = new ActionCode();
        code.setCode("1115613");
        Concept c = new Concept();
        c.setLabel("Code OA");
        code.setType(c);
        List<ActionCode> codes = List.of(code);
        return codes;

    }

    /**
     * Fetch the autocomplete results on API for the type field
     *
     * @param input the input of the user
     * @return the list of concepts that match the input to display in the autocomplete
     */
    public List<Concept> completeActionCodeType(String input) {

        try {
            return fieldConfigurationService.fetchAutocomplete(sessionSettings.getUserInfo(), ActionCode.TYPE_FIELD_CODE, input);
        } catch (NoConfigForField e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }

    }

    public void handleSelectPrimaryCode() {
        return;
    }

    public void addNewSecondaryCode() {
        ActionCode code = new ActionCode();
        Concept c = new Concept();
        c.setLabel("Code OA");
        code.setCode("1115613zz");
        code.setType(c);
        secondaryActionCodes.add(code);
    }

    public void removeSecondaryCode(int index) {
        secondaryActionCodes.remove(index);
    }

    public void save() {
        try {
            Person author = sessionSettings.getAuthenticatedUser();
            actionUnit.setLastModifiedBy(author);

            this.actionUnit = actionUnitService.save(actionUnit, secondaryActionCodes);

            // Display message
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Info",
                            langBean.msg("actionunit.created", this.actionUnit.getName())));
//
//            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

            //return "/pages/actionUnit/actionUnit?faces-redirect=true&id=" + this.actionUnit.getId().toString();

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("actionunit.creationfailed", this.actionUnit.getName())));

            log.error("Error while saving: " + e.getMessage());
        }
    }

    public void init() {

        if (!FacesContext.getCurrentInstance().isPostback()) {
            // reinit
            actionUnitErrorMessage = null;
            actionUnit = null;

            c1.setLabel("Code OA");
            c2.setLabel("Code OP");
            actionCodeTypeOptions = List.of(c1,c2);
            secondaryActionCodes = new ArrayList<>();
            // Get the requested action from DB
            try {
                if(id!=null) {
                    actionUnit = actionUnitService.findById(id);
                    ActionCode primaryActionCode = new ActionCode();
                    actionUnit.setPrimaryActionCode(primaryActionCode);

                    Concept typeConcept = actionUnit.getType();
                    fType = new ConceptFieldDTO();
                    fType.setLabel(typeConcept.getLabel());
                    // If thesaurus we can reconstruct the DTO
                    fType.setUri(typeConcept.getVocabulary().getBaseUri()+"?idc="+typeConcept.getExternalId()+"&idt="+typeConcept.getVocabulary().getExternalVocabularyId());

                }
                else {
                    this.actionUnitErrorMessage = "No action unit ID specified";
                }
            }
            catch (RuntimeException e) {
                this.actionUnitErrorMessage = "Failed to load action unit: " + e.getMessage();
            }

        }


    }


}
