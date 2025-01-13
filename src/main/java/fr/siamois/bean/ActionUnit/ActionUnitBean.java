package fr.siamois.bean.ActionUnit;

import fr.siamois.bean.LangBean;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.ActionUnit;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.NoConfigForField;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.vocabulary.FieldConfigurationWrapper;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
import fr.siamois.utils.AuthenticatedUserUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.time.OffsetDateTime.now;


@Slf4j
@Getter
@Setter
@Component
@SessionScoped
public class ActionUnitBean implements Serializable {

    // Deps
    private final ActionUnitService actionUnitService;

    // Local
    private ActionUnit actionUnit;
    private String actionUnitErrorMessage;
    private Long id;  // ID of the action unit requested

    public ActionUnitBean(ActionUnitService actionUnitService) {
        this.actionUnitService = actionUnitService;
    }

    public void init() {

        // reinit
        actionUnitErrorMessage = null;
        actionUnit = null;

       // Get the request action from DB
        try {
            if(id!=null) {
                actionUnit = actionUnitService.findById(id);
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
