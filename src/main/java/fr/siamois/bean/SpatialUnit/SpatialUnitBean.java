package fr.siamois.bean.SpatialUnit;

import fr.siamois.models.ActionUnit;
import fr.siamois.models.RecordingUnit;
import fr.siamois.models.SpatialUnit;

import fr.siamois.services.ActionUnitService;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.services.RecordingUnitService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.List;

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
    String spatialUnitErrorMessage;
    @Getter
    private List<SpatialUnit> spatialUnitList;
    @Getter
    private List<RecordingUnit> recordingUnitList;
    @Getter
    private List<ActionUnit> actionUnitList;
    @Getter
    String spatialUnitListErrorMessage;
    @Getter
    String actionUnitListErrorMessage;
    @Getter
    String recordingUnitListErrorMessage;

    @Getter
    @Setter
    private Long id;  // ID of the spatial unit

    @PostConstruct
    public void init() {
        if (id != null) {

            try {
                spatialUnit = spatialUnitService.findById(id);
            } catch (RuntimeException e) {
                spatialUnitErrorMessage = "Failed to load spatial unit: " + e.getMessage();
            }

            if (spatialUnit != null) {
                try {
                    spatialUnitList = spatialUnitService.findAllChildOfSpatialUnit(spatialUnit);
                } catch (RuntimeException e) {
                    spatialUnitListErrorMessage = "Unable to load spatial units: " + e.getMessage();
                }
                try {
                    recordingUnitList = recordingUnitService.findAllBySpatialUnitId(spatialUnit);
                } catch (RuntimeException e) {
                    recordingUnitListErrorMessage = "Unable to load recording units: " + e.getMessage();
                }
                try {
                    actionUnitList = actionUnitService.findAllBySpatialUnitId(spatialUnit);
                } catch (RuntimeException e) {
                    actionUnitListErrorMessage = "Unable to load action units: " + e.getMessage();
                }
            }

        }
    }

}
