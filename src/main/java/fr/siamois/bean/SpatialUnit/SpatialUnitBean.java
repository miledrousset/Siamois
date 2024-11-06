package fr.siamois.bean.SpatialUnit;

import fr.siamois.models.ActionUnit;
import fr.siamois.models.RecordingUnit;
import fr.siamois.models.SpatialUnit;

import fr.siamois.services.ActionUnitService;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.services.RecordingUnitService;
import jakarta.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@SessionScoped
public class SpatialUnitBean {

    @Autowired
    private SpatialUnitService spatialUnitService;
    @Autowired
    private RecordingUnitService recordingUnitService;
    @Autowired
    private ActionUnitService actionUnitService;

    @Getter
    private SpatialUnit spatialUnit;
    @Getter
    private List<SpatialUnit> spatialUnitList;
    @Getter
    private List<RecordingUnit> recordingUnitList;
    @Getter
    private List<ActionUnit> actionUnitList;

    @Getter @Setter
    private Integer id;  // ID of the spatial unit



    @PostConstruct
    public void init() {
        if (id != null) {
            SpatialUnit optionalSpatialUnit = spatialUnitService.findById(id);
            // Handle the case when the spatial unit is not found
/*            spatialUnit = optionalSpatialUnit.orElse(null);
            if(spatialUnit != null) {
                spatialUnitList = spatialUnitService.findAllChildOfSpatialUnit(spatialUnit);
                recordingUnitList = recordingUnitService.findAllBySpatialUnitId(spatialUnit);
                actionUnitList = actionUnitService.findAllBySpatialUnitId(spatialUnit);
            }*/
        }
    }
}
