package fr.siamois.ui.bean.panel;

import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.DocumentCreationBean;
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
    private final DocumentService documentService;
    private final DocumentCreationBean documentCreationBean;


    public PanelFactory(
            SpatialUnitService spatialUnitService,
            RecordingUnitService recordingUnitService,
            ActionUnitService actionUnitService,
            SessionSettingsBean sessionSettings,
            SpatialUnitHelperService spatialUnitHelperService, DocumentService documentService, DocumentCreationBean documentCreationBean) {
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.sessionSettings = sessionSettings;
        this.spatialUnitHelperService = spatialUnitHelperService;
        this.documentService = documentService;
        this.documentCreationBean = documentCreationBean;
    }

    public SpatialUnitPanel createSpatialUnitPanel(Long spatialUnitId, PanelBreadcrumb currentBreadcrumb) {
        return SpatialUnitPanel.builder()
                .spatialUnitService(spatialUnitService)
                .recordingUnitService(recordingUnitService)
                .actionUnitService(actionUnitService)
                .sessionSettings(sessionSettings)
                .id(spatialUnitId)
                .currentBreadcrumb(currentBreadcrumb)
                .spatialUnitHelperService(spatialUnitHelperService)
                .documentService(documentService)
                .documentCreationBean(documentCreationBean)
                .build();
    }

    public SpatialUnitListPanel createSpatialUnitListPanel() {
        return new SpatialUnitListPanel(
                spatialUnitService,
                sessionSettings);
    }

}