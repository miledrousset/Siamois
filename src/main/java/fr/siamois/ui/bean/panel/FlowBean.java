package fr.siamois.ui.bean.panel;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.recordingunit.StratigraphicRelationshipService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.FieldService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.breadcrumb.BreadcrumbBean;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import fr.siamois.ui.bean.panel.models.panel.ActionUnitPanel;
import fr.siamois.ui.bean.panel.models.panel.SpatialUnitListPanel;
import fr.siamois.ui.bean.panel.models.panel.SpatialUnitPanel;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.dashboard.DashboardModel;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This ui.bean handles the home page</p>
 * <p>It is used to display the list of spatial units without parents</p>
 *
 * @author Grégory Bliault
 */
@Slf4j
@Component
@SessionScoped
@Data
public class FlowBean implements Serializable {


    private final transient SpatialUnitService spatialUnitService;
    private final transient RecordingUnitService recordingUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient HistoryService historyService;
    private final transient SessionSettingsBean sessionSettings;
    private final transient LangBean langBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient FieldService fieldService;
    private final transient PanelFactory panelFactory;
    private final transient PersonService personService;
    private final transient ConceptService conceptService;
    private final transient StratigraphicRelationshipService stratigraphicRelationshipService;
    private final transient BreadcrumbBean breadcrumbBean;

    // locals
    private transient DashboardModel responsiveModel;
    private static final String RESPONSIVE_CLASS = "col-12 lg:col-6 xl:col-6";

    // Search bar
    private List<SpatialUnit> fSpatialUnits = List.of();
    private List<ActionUnit> fActionUnits = List.of();
    private SpatialUnit fSelectedSpatialUnit;
    private ActionUnit fSelectedActionUnit;

    @Getter
    private transient List<AbstractPanel> panels = new ArrayList<>();
    private transient int fullscreenPanelIndex = -1;

    public FlowBean(SpatialUnitService spatialUnitService,
                    RecordingUnitService recordingUnitService,
                    ActionUnitService actionUnitService,
                    HistoryService historyService,
                    SessionSettingsBean sessionSettings,
                    LangBean langBean,
                    FieldConfigurationService fieldConfigurationService,
                    FieldService fieldService,
                    PanelFactory panelFactory,
                    PersonService personService,
                    ConceptService conceptService,
                    StratigraphicRelationshipService stratigraphicRelationshipService, BreadcrumbBean breadcrumbBean) {
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.historyService = historyService;
        this.sessionSettings = sessionSettings;
        this.langBean = langBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.fieldService = fieldService;
        this.panelFactory = panelFactory;
        this.personService = personService;
        this.conceptService = conceptService;
        this.stratigraphicRelationshipService = stratigraphicRelationshipService;
        this.breadcrumbBean = breadcrumbBean;
    }

    public void init() {
        UserInfo info =  sessionSettings.getUserInfo();
        fSpatialUnits = spatialUnitService.findAllOfInstitution(info.getInstitution());
    }

    public void addSpatialUnitListPanel() {
        panels.add(0, panelFactory.createSpatialUnitListPanel());
    }

    public void addNewSpatialUnitPanel(AbstractPanel currentPanel) {
        panels.add(0, panelFactory.createNewSpatialUnitPanel(currentPanel.getBreadcrumb()));
    }

    public void addNewActionUnitPanel(Long spatialUnitId, Integer sourcePanelIndex) {
        panels.add(0, panelFactory.createNewActionUnitPanel(spatialUnitId, panels.get(sourcePanelIndex).getBreadcrumb()));
    }

    public void addNewRecordingUnitPanel(Long actionUnitId, Integer sourcePanelIndex) {
        panels.add(0, panelFactory.createNewRecordingUnitPanel(actionUnitId, panels.get(sourcePanelIndex).getBreadcrumb()));
    }


    public void goToHomeCurrentPanel(AbstractPanel panel) {
        // Change current panel type add item to its breadcrumb
        int index = panels.indexOf(panel);
        SpatialUnitListPanel newPanel = panelFactory.createSpatialUnitListPanel();
        panels.set(index, newPanel);
    }

    public void goToSpatialUnitByIdNewPanel(Long id, AbstractPanel currentPanel) {
        // Create new panel type and add items to its breadcrumb
        SpatialUnitPanel newPanel = panelFactory.createSpatialUnitPanel(id, currentPanel.getBreadcrumb());
        panels.add(0, newPanel);
    }

    public void goToActionUnitByIdNewPanel(Long id, Integer currentPanelIndex) {
        // Create new panel type and add items to its breadcrumb
        ActionUnitPanel newPanel = panelFactory.createActionUnitPanel(id, panels.get(currentPanelIndex).getBreadcrumb());
        panels.add(0, newPanel);
    }

    public void goToSpatialUnitByIdCurrentPanel(Long id, AbstractPanel currentPanel) {
        // Change current panel type add item to its breadcrumb
        int index = panels.indexOf(currentPanel);
        if (index != -1) {
            SpatialUnitPanel newPanel = panelFactory.createSpatialUnitPanel(id, currentPanel.getBreadcrumb());
            panels.set(index, newPanel);
        }

    }

    public void goToActionUnitByIdCurrentPanel(Long id, Integer currentPanelIndex) {
        // Change current panel type add item to its breadcrumb
        int index = currentPanelIndex;
        ActionUnitPanel newPanel = panelFactory.createActionUnitPanel(id, panels.get(currentPanelIndex).getBreadcrumb());
        panels.set(index, newPanel);


    }

    public void fullScreen(AbstractPanel panel) {
        // Could use setter if we don't add more code
        int index = panels.indexOf(panel);
        if (index != -1) {
            fullscreenPanelIndex = index;
        }
    }

    public void closeFullScreen() {
        fullscreenPanelIndex = -1;
    }


}