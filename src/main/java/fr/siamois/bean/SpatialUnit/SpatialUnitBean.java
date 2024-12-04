package fr.siamois.bean.SpatialUnit;

import fr.siamois.models.ActionUnit;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.services.ActionUnitService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.SpatialUnitService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

@Slf4j
@Component
@SessionScoped
public class SpatialUnitBean implements Serializable {

    private final SpatialUnitService spatialUnitService;
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;

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

    public SpatialUnitBean(SpatialUnitService spatialUnitService, RecordingUnitService recordingUnitService, ActionUnitService actionUnitService) {
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
    }

    public void reinitializeBean() {
        this.spatialUnit = null;
        this.spatialUnitErrorMessage = null;

        this.spatialUnitListErrorMessage = null;
        this.recordingUnitListErrorMessage = null;
        this.actionUnitListErrorMessage = null;
        this.spatialUnitList = null;
        this.recordingUnitList = null;
        this.actionUnitList = null;
    }

    @PostConstruct
    public void init() {

        reinitializeBean();

        if (id != null) {

            try {
                this.spatialUnit = spatialUnitService.findById(id);
            } catch (RuntimeException e) {
                this.spatialUnitErrorMessage = "Failed to load spatial unit: " + e.getMessage();
            }

            if (this.spatialUnit != null) {
                try {
                    this.spatialUnitListErrorMessage = null;
                    this.spatialUnitList = spatialUnitService.findAllChildOfSpatialUnit(spatialUnit);
                } catch (RuntimeException e) {
                    this.spatialUnitList = null;
                    this.spatialUnitListErrorMessage = "Unable to load spatial units: " + e.getMessage();
                }
                try {
                    this.recordingUnitListErrorMessage = null;
                    this.recordingUnitList = recordingUnitService.findAllBySpatialUnit(spatialUnit);
                } catch (RuntimeException e) {
                    this.recordingUnitList = null;
                    this.recordingUnitListErrorMessage = "Unable to load recording units: " + e.getMessage();
                }
                try {
                    this.actionUnitListErrorMessage = null;
                    this.actionUnitList = actionUnitService.findAllBySpatialUnitId(spatialUnit);
                } catch (RuntimeException e) {
                    this.actionUnitList = null;
                    this.actionUnitListErrorMessage = "Unable to load action units: " + e.getMessage();
                }
            }

        }
        else {
            this.spatialUnitErrorMessage = "The ID of the spatial unit must be defined";
        }
    }

}
