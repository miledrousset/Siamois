package fr.siamois.ui.bean.panel;

import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.SpatialUnitListPanel;
import fr.siamois.ui.bean.panel.models.SpatialUnitPanel;
import org.springframework.stereotype.Component;

import javax.faces.bean.ApplicationScoped;

@Component
@ApplicationScoped
public class PanelFactory {

    private final SpatialUnitService spatialUnitService;
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;
    private final HistoryService historyService;
    private final SessionSettingsBean sessionSettings;


    public PanelFactory(SpatialUnitService spatialUnitService, RecordingUnitService recordingUnitService, ActionUnitService actionUnitService, HistoryService historyService, SessionSettingsBean sessionSettings) {
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.historyService = historyService;
        this.sessionSettings = sessionSettings;
    }

    public SpatialUnitPanel createSpatialUnitPanel(Long spatialUnitId) {
        return new SpatialUnitPanel(
                spatialUnitService,
                recordingUnitService,
                actionUnitService,
                historyService,
                sessionSettings,
                spatialUnitId);
    }

    public SpatialUnitListPanel createSpatialUnitListPanel() {
        return new SpatialUnitListPanel(
                spatialUnitService,
                sessionSettings);
    }
}