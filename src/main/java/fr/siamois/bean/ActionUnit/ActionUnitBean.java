package fr.siamois.bean.ActionUnit;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.ActionUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.ActionUnitService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;


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
