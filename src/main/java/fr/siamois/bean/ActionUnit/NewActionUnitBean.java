package fr.siamois.bean.ActionUnit;

import fr.siamois.bean.RecordingUnit.NewRecordingUnitFormBean;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.ActionUnit;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.recordingunit.RecordingUnitAltimetry;
import fr.siamois.models.recordingunit.RecordingUnitSize;
import fr.siamois.models.vocabulary.FieldConfigurationWrapper;
import fr.siamois.services.ActionUnitService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.time.OffsetDateTime.now;

@Data
@Slf4j
@SessionScoped
@Component
public class NewActionUnitBean implements Serializable {

    // Deps
    private final ActionUnitService actionUnitService;

    // Local
    private ActionUnit actionUnit;
    private List<ConceptFieldDTO> concepts;
    private ConceptFieldDTO fType = null;
    private FieldConfigurationWrapper configurationWrapper;

    public NewActionUnitBean(ActionUnitService actionUnitService) {
        this.actionUnitService = actionUnitService;
    }

    public void init(SpatialUnit spatialUnit) {

        this.actionUnit = new ActionUnit();
        this.actionUnit.setSpatialUnit(spatialUnit);

    }


}
