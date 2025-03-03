package fr.siamois.bean.spatialunit;

import fr.siamois.bean.SessionSettings;
import fr.siamois.services.HistoryService;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.services.actionunit.ActionUnitService;
import fr.siamois.services.recordingunit.RecordingUnitService;
import org.springframework.stereotype.Component;

@Component
public class PanelFactory {

    private final transient SpatialUnitService spatialUnitService;
    private final transient RecordingUnitService recordingUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient HistoryService historyService;
    private final SessionSettings sessionSettings;


    public PanelFactory(SpatialUnitService spatialUnitService, RecordingUnitService recordingUnitService, ActionUnitService actionUnitService, HistoryService historyService, SessionSettings sessionSettings) {
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.historyService = historyService;
        this.sessionSettings = sessionSettings;
    }

    public SpatialUnitPanel createSpatialUnitPanel(Long spatialUnitId) {
        return new SpatialUnitPanel(spatialUnitService, recordingUnitService, actionUnitService, historyService, sessionSettings, spatialUnitId);
    }
}
