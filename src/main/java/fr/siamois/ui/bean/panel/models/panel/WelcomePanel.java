package fr.siamois.ui.bean.panel.models.panel;


import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.ui.bean.SessionSettingsBean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class WelcomePanel extends AbstractPanel {

    private final SessionSettingsBean sessionSettingsBean;
    private final transient RecordingUnitService recordingUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient SpatialUnitService spatialUnitService;

    // Locals
    private long nbOfSpatialUnits;
    private long nbOfActionUnits;
    private long nbOfRecordingUnits;

    public WelcomePanel(SessionSettingsBean sessionSettingsBean, RecordingUnitService recordingUnitService, ActionUnitService actionUnitService, SpatialUnitService spatialUnitService) {
        super("Accueil", "bi bi-house", "siamois-panel");

        this.sessionSettingsBean = sessionSettingsBean;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.spatialUnitService = spatialUnitService;

        setBreadcrumb(null);
        setIsBreadcrumbVisible(false);
        init();

    }

    public void init() {

        // Get the list of spatial, action and recording unit in the orga
        nbOfActionUnits = 0;
        nbOfSpatialUnits = 0;
        nbOfRecordingUnits = 0;

        try {
            nbOfRecordingUnits = recordingUnitService.countByInstitution(sessionSettingsBean.getSelectedInstitution());
            nbOfActionUnits = actionUnitService.countByInstitution(sessionSettingsBean.getSelectedInstitution());
            nbOfSpatialUnits = spatialUnitService.countByInstitution(sessionSettingsBean.getSelectedInstitution());
        }
        catch(RuntimeException e) {
            log.error(e.getMessage());
        }
    }


    @Override
    public String display() {
        return "/panel/homePanel.xhtml";
    }
}
