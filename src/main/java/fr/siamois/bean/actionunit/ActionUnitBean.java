package fr.siamois.bean.actionunit;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.actionunit.ActionCode;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.actionunit.ActionUnitService;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
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
    private final transient ActionUnitService actionUnitService;
    private final LangBean langBean;
    private final SessionSettings sessionSettings;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient FieldService fieldService;

    // Local
    private ActionUnit actionUnit;
    private String actionUnitErrorMessage;
    private Long id;  // ID of the action unit requested

    // For entering new code
    private ActionCode newCode;
    private Integer newCodeIndex; // Index of the new code, if primary: 0, otherwise 1 to N
    // (but corresponds to 0 to N-1 in secondary code list)

    // Field related
    private Boolean editType;
    private ConceptFieldDTO fType;

    // todo: remove below and implement properly
    private Concept c1 = new Concept();
    private Concept c2 = new Concept();
    private transient List<Concept> actionCodeTypeOptions ;
    private transient List<ActionCode> secondaryActionCodes ;


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

        return actionUnitService.findAllActionCodeByCodeIsContainingIgnoreCase(input);

    }

    /**
     * Fetch the autocomplete results on API for the action code type field
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
        // To implement
    }

    public void addNewSecondaryCode() {
        ActionCode code = new ActionCode();
        Concept c = new Concept();
        code.setCode("");
        code.setType(c);
        secondaryActionCodes.add(code);
    }

    public void initNewActionCode(int index) {
        newCodeIndex = index;
        newCode = new ActionCode();
    }

    public void removeSecondaryCode(int index) {
        secondaryActionCodes.remove(index);
    }

    public void save() {
        try {
            Person author = sessionSettings.getAuthenticatedUser();
            actionUnit.setLastModifiedBy(author);

            this.actionUnit = actionUnitService.save(actionUnit, secondaryActionCodes, sessionSettings.getUserInfo());

            // Display message
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Info",
                            langBean.msg("actionunit.created", this.actionUnit.getName())));

        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error",
                            langBean.msg("actionunit.creationfailed", this.actionUnit.getName())));

            log.error("Error while saving: " + e.getMessage());
        }
    }

    public void saveNewActionCode() {
        // Update the action code
        if(newCodeIndex == 0) {
            // update primary action code
            actionUnit.setPrimaryActionCode(newCode);
        } else if (newCodeIndex > 0) {
            actionUnit.getSecondaryActionCodes().add(newCode);
            secondaryActionCodes.set(newCodeIndex-1, newCode);
        }
    }

    public void init() {

        if (!FacesContext.getCurrentInstance().isPostback()) {
            // reinit
            actionUnitErrorMessage = null;
            actionUnit = null;
            newCode = new ActionCode();
            secondaryActionCodes = new ArrayList<>();
            // Get the requested action from DB
            try {
                if(id!=null) {
                    actionUnit = actionUnitService.findById(id);
                    Concept typeConcept = actionUnit.getType();
                    secondaryActionCodes = new ArrayList<>(actionUnit.getSecondaryActionCodes());
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
