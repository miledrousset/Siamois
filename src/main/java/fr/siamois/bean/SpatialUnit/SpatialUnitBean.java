package fr.siamois.bean.SpatialUnit;

import fr.siamois.bean.SessionSettings;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.Team;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.history.SpatialUnitHist;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.services.actionunit.ActionUnitService;
import fr.siamois.services.HistoryService;
import fr.siamois.services.RecordingUnitService;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.utils.DateUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * <p>This bean handles the spatial unit page</p>
 *
 * @author Grégory Bliault
 */
@Slf4j
@Component
@SessionScoped
@Data
public class SpatialUnitBean implements Serializable {

    private final SpatialUnitService spatialUnitService;
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;
    private final HistoryService historyService;
    private final SessionSettings sessionSettings;

    private SpatialUnit spatialUnit;
    private String spatialUnitErrorMessage;
    private List<SpatialUnit> spatialUnitList;
    private List<SpatialUnit> spatialUnitParentsList;
    private List<RecordingUnit> recordingUnitList;
    private List<ActionUnit> actionUnitList;
    private String spatialUnitListErrorMessage;
    private String spatialUnitParentsListErrorMessage;
    private String actionUnitListErrorMessage;
    private String recordingUnitListErrorMessage;

    private List<SpatialUnitHist> historyVersion;

    private SpatialUnitHist revisionToDisplay = null;

    private Long id;  // ID of the spatial unit

    public SpatialUnitBean(SpatialUnitService spatialUnitService, RecordingUnitService recordingUnitService, ActionUnitService actionUnitService, HistoryService historyService, SessionSettings sessionSettings) {
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.historyService = historyService;
        this.sessionSettings = sessionSettings;
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
        this.spatialUnitParentsList = null;
        this.spatialUnitParentsListErrorMessage = null;
    }

    public String goToSpatialUnitById(Long id) {
        log.trace("go to spatial unit");
        return "/pages/spatialUnit/spatialUnit.xhtml?id=" + id+"&faces-redirect=true";
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
                    this.spatialUnitParentsListErrorMessage = null;
                    this.spatialUnitParentsList = spatialUnitService.findAllParentsOfSpatialUnit(spatialUnit);
                } catch (RuntimeException e) {
                    this.spatialUnitParentsList = null;
                    this.spatialUnitParentsListErrorMessage = "Unable to load the parents: " + e.getMessage();
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
                historyVersion = historyService.findSpatialUnitHistory(spatialUnit);
            }

        }
        else {
            this.spatialUnitErrorMessage = "The ID of the spatial unit must be defined";
        }
    }

    public String formatDate(OffsetDateTime offsetDateTime) {
        return DateUtils.formatOffsetDateTime(offsetDateTime);
    }

    public void visualise(SpatialUnitHist history) {
        log.trace("History version changed to {}", history.toString());
        revisionToDisplay = history;
    }

    public void restore(SpatialUnitHist history) {
        log.trace("Restore order received");
        spatialUnitService.restore(history);
        PrimeFaces.current().executeScript("PF('restored-dlg').show()");
    }

}
