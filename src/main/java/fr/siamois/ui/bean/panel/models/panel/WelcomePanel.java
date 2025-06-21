package fr.siamois.ui.bean.panel.models.panel;


import fr.siamois.domain.models.events.LangageChangeEvent;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
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
    private final LangBean langBean;

    // Locals
    private long nbOfSpatialUnits;
    private long nbOfActionUnits;
    private long nbOfRecordingUnits;

    public WelcomePanel(SessionSettingsBean sessionSettingsBean,
                        RecordingUnitService recordingUnitService,
                        ActionUnitService actionUnitService,
                        SpatialUnitService spatialUnitService,
                        LangBean langBean
    ) {
        super("common.location.home", "bi bi-house", "siamois-panel");

        this.sessionSettingsBean = sessionSettingsBean;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.spatialUnitService = spatialUnitService;
        this.langBean = langBean;

        setBreadcrumb(new PanelBreadcrumb());
        setIsBreadcrumbVisible(false);
        init();

    }

    public void init() {

        // Get the list of spatial, action and recording unit in the orga
        nbOfActionUnits = 0;
        nbOfSpatialUnits = 0;
        nbOfRecordingUnits = 0;
        refreshName();

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

    @Override
    public String ressourceUri() {
        return "/welcome";
    }

    @Override
    public String displayHeader() {
        return "/panel/header/homePanelHeader.xhtml";
    }

    @EventListener(LangageChangeEvent.class)
    public void refreshName() {
        this.titleCodeOrTitle = String.format("%s - %s",
                langBean.msg("common.location.home"),
                sessionSettingsBean.getSelectedInstitution().getName());
    }

}
