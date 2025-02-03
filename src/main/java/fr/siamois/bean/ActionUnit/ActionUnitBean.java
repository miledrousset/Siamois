package fr.siamois.bean.ActionUnit;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.actionunit.ActionCode;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.ActionUnitService;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Data
@Component
@SessionScoped
public class ActionUnitBean implements Serializable {

    // Deps
    private final ActionUnitService actionUnitService;

    // Local
    private ActionUnit actionUnit;
    private String actionUnitErrorMessage;
    private Long id;  // ID of the action unit requested

    // Field related
    private Boolean editType;
    private ConceptFieldDTO fType;


    public ActionUnitBean(ActionUnitService actionUnitService) {
        this.actionUnitService = actionUnitService;
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


    public void init() {

        // reinit
        actionUnitErrorMessage = null;
        actionUnit = null;

       // Get the requested action from DB
        try {
            if(id!=null) {
                actionUnit = actionUnitService.findById(id);
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
