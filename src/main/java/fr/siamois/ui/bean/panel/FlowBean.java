package fr.siamois.ui.bean.panel;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.events.InstitutionChangeEvent;
import fr.siamois.domain.models.events.LoginEvent;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.authorization.PermissionService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.recordingunit.StratigraphicRelationshipService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.FieldService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.breadcrumb.BreadcrumbBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.panel.models.panel.*;
import fr.siamois.ui.bean.panel.models.panel.single.ActionUnitPanel;
import fr.siamois.ui.bean.panel.models.panel.single.RecordingUnitPanel;
import fr.siamois.ui.bean.panel.models.panel.single.SpatialUnitPanel;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.ui.lazydatamodel.BaseSpatialUnitLazyDataModel;
import jakarta.el.MethodExpression;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.dashboard.DashboardModel;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.event.EventListener;
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
    private final SessionSettingsBean sessionSettings;
    private final LangBean langBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient FieldService fieldService;
    private final transient PanelFactory panelFactory;
    private final transient PersonService personService;
    private final transient ConceptService conceptService;
    private final transient StratigraphicRelationshipService stratigraphicRelationshipService;
    private final BreadcrumbBean breadcrumbBean;
    private final transient PermissionService permissionService;

    // locals
    private transient DashboardModel responsiveModel;
    private static final String RESPONSIVE_CLASS = "col-12 lg:col-6 xl:col-6";
    private Integer lastUpdatedPanelIndex = 0;
    private String readWriteMode = "READ";

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
                    StratigraphicRelationshipService stratigraphicRelationshipService, BreadcrumbBean breadcrumbBean, PermissionService permissionService) {
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
        this.permissionService = permissionService;
    }


    public void init() {
        fullscreenPanelIndex = -1;
        panels = new ArrayList<>();
        addWelcomePanel();
        Institution institution = sessionSettings.getSelectedInstitution();
        fSpatialUnits = spatialUnitService.findAllOfInstitution(institution);
    }

    @EventListener(InstitutionChangeEvent.class)
    public void handleInstitutionChange() {
        init();
    }

    @EventListener(LoginEvent.class)
    public void handleLoginSuccess() {
        init();
    }

    public void addSpatialUnitListPanel(PanelBreadcrumb bc) {
        addPanel(panelFactory.createSpatialUnitListPanel(bc));
    }

    public void addActionUnitListPanel(PanelBreadcrumb bc) {
        addPanel(panelFactory.createActionUnitListPanel(bc));
    }

    public void addRecordingUnitListPanel(PanelBreadcrumb bc) {
        addPanel(panelFactory.createRecordingUnitListPanel(bc));
    }


    public void addPanel(AbstractPanel panel) {
        panels.add(0, panel);
        lastUpdatedPanelIndex = 0;
    }

    public void addWelcomePanel() {
        // We find the index of the welcome panel in the flow, if it does not exist we add it,
        // otherwise we move it on top.
        if (panels == null || panels.isEmpty()) {
            panels = new ArrayList<>();
        }

        // Find the index of the first object of the desired type
        int indexToMove = -1;
        for (int i = 0; i < panels.size(); i++) {
            if (panels.get(i) instanceof WelcomePanel) {
                indexToMove = i;
                break;
            }
        }

        // If found and not already at index 0, move it to the top
        if (indexToMove >= 0) {
            panels.remove(indexToMove);
        }

        // Add a new instance to refresh the panel
        addPanel(panelFactory.createWelcomePanel());

    }

    public void addNewSpatialUnitPanel(AbstractPanel currentPanel) {
        addPanel(panelFactory.createNewSpatialUnitPanel(currentPanel.getBreadcrumb()));
    }

    public void addNewSpatialUnitPanel(AbstractPanel currentPanel, BaseSpatialUnitLazyDataModel lazyModel) {
        addPanel(panelFactory.createNewSpatialUnitPanel(currentPanel.getBreadcrumb(),lazyModel));
    }

    public void addNewSpatialUnitPanel() {
        addPanel(panelFactory.createNewSpatialUnitPanel(null));
    }

    public void addNewActionUnitPanel(Integer sourcePanelIndex) {
        addPanel(panelFactory.createNewActionUnitPanel(panels.get(sourcePanelIndex).getBreadcrumb()));
    }

    public void addNewActionUnitPanel(Long spatialUnitId, Integer sourcePanelIndex) {
        addPanel(panelFactory.createNewActionUnitPanel(spatialUnitId, panels.get(sourcePanelIndex).getBreadcrumb()));
    }

    public void addNewActionUnitPanel(Long spatialUnitId) {
        addPanel(panelFactory.createNewActionUnitPanel(spatialUnitId, null));
    }

    public void addActionUnitPanel(Long actionUnitId) {
        addPanel(panelFactory.createActionUnitPanel(actionUnitId));
    }

    public void addRecordingUnitPanel(Long recordingUnitId) {
        addPanel(panelFactory.createRecordingUnitPanel(recordingUnitId));
    }


    public void goToSpatialUnitByIdNewPanel(Long id, AbstractPanel currentPanel) {
        // Create new panel type and add items to its breadcrumb
        SpatialUnitPanel newPanel = panelFactory.createSpatialUnitPanel(id, currentPanel.getBreadcrumb());
        addPanel(newPanel);
    }

    public void  goToRecordingUnitByIdCurrentPanel(Long id, Integer currentPanelIndex) {

        RecordingUnitPanel newPanel = panelFactory.createRecordingUnitPanel(id, panels.get(currentPanelIndex).getBreadcrumb());
        panels.set(currentPanelIndex, newPanel);
        lastUpdatedPanelIndex = currentPanelIndex;

    }

    public void  goToRecordingUnitByIdNewPanel(Long id, Integer currentPanelIndex) {

        RecordingUnitPanel newPanel = panelFactory.createRecordingUnitPanel(id, panels.get(currentPanelIndex).getBreadcrumb());
        addPanel(newPanel);

    }

    public void goToActionUnitByIdNewPanel(Long id, Integer currentPanelIndex) {
        // Create new panel type and add items to its breadcrumb
        ActionUnitPanel newPanel = panelFactory.createActionUnitPanel(id, panels.get(currentPanelIndex).getBreadcrumb());
        addPanel(newPanel);
    }

    public void goToSpatialUnitByIdCurrentPanel(Long id, AbstractPanel currentPanel) {
        // Change current panel type add item to its breadcrumb
        int index = panels.indexOf(currentPanel);
        if (index != -1) {
            SpatialUnitPanel newPanel = panelFactory.createSpatialUnitPanel(id, currentPanel.getBreadcrumb());
            panels.set(index, newPanel);
            lastUpdatedPanelIndex = index;
        }

    }

    public void goToActionUnitByIdCurrentPanel(Long id, Integer currentPanelIndex) {
        // Change current panel type add item to its breadcrumb
        int index = currentPanelIndex;
        ActionUnitPanel newPanel = panelFactory.createActionUnitPanel(id, panels.get(currentPanelIndex).getBreadcrumb());
        panels.set(index, newPanel);
        lastUpdatedPanelIndex = index;


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


    public void addSpatialUnitPanel(Long id) {
        addPanel(panelFactory.createSpatialUnitPanel(id));
    }

    public void handleToggleOfPanelAtIndex(int idx)
    {
        AbstractPanel panel = panels.get(idx);
        panel.setCollapsed(!panel.getCollapsed());
    }

    public void closePanelAtIndex(int idx) {
        panels.remove(idx);
    }

    public void changeReadWriteMode() {
        // Empty method updating read/write.
    }

    public String getInPlaceFieldMode() {
        if (readWriteMode.equals("WRITE")) {
            return "input";
        }
        return "output";
    }

    public String headerName(AbstractPanel panel) {
        try {
            return langBean.msg(panel.getTitleCodeOrTitle());
        } catch (NoSuchMessageException e) {
            return panel.getTitleCodeOrTitle();
        }
    }

    public boolean userHasAddSpatialOrActionUnitPermission() {
        UserInfo info = sessionSettings.getUserInfo();
        return info.getUser().isSuperAdmin() || permissionService.isActionManager(info) || permissionService.isInstitutionManager(info);
    }

    public String invokeOnClick(MethodExpression method, Long id, AbstractPanel panelModel) {
        if (method != null) {
            method.invoke(FacesContext.getCurrentInstance().getELContext(), new Object[]{id, panelModel});
        }
        return null; // for commandLink action return
    }
}