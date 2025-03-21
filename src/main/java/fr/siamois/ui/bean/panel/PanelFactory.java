package fr.siamois.ui.bean.panel;

import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.form.CustomFieldService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.panel.models.panel.SpatialUnitListPanel;
import fr.siamois.ui.bean.panel.models.panel.SpatialUnitPanel;
import fr.siamois.ui.bean.panel.utils.SpatialUnitHelperService;
import org.springframework.stereotype.Component;

import javax.faces.bean.ApplicationScoped;

@Component
@ApplicationScoped
public class PanelFactory {

    private final SpatialUnitService spatialUnitService;
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;
    private final SessionSettingsBean sessionSettings;
    private final SpatialUnitHelperService spatialUnitHelperService;
    private final CustomFieldService customFieldService;


    public PanelFactory(
            SpatialUnitService spatialUnitService,
            RecordingUnitService recordingUnitService,
            ActionUnitService actionUnitService,
            SessionSettingsBean sessionSettings,
            SpatialUnitHelperService spatialUnitHelperService, CustomFieldService customFieldService) {
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.sessionSettings = sessionSettings;
        this.spatialUnitHelperService = spatialUnitHelperService;
        this.customFieldService = customFieldService;
    }

    public SpatialUnitPanel createSpatialUnitPanel(Long spatialUnitId, PanelBreadcrumb currentBreadcrumb) {
        return new SpatialUnitPanel(
                spatialUnitService,
                recordingUnitService,
                actionUnitService,
                sessionSettings,
                spatialUnitId,
                currentBreadcrumb,
                spatialUnitHelperService,
                customFieldService);
    }

    public SpatialUnitListPanel createSpatialUnitListPanel() {
        return new SpatialUnitListPanel(
                spatialUnitService,
                sessionSettings);
    }

}