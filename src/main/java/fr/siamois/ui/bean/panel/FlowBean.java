package fr.siamois.ui.bean.panel;

import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.breadcrumb.BreadcrumbBean;
import fr.siamois.ui.bean.panel.models.panel.AbstractPanel;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.PersonService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.recordingunit.StratigraphicRelationshipService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.FieldService;
import fr.siamois.ui.bean.panel.models.panel.SpatialUnitListPanel;
import fr.siamois.ui.bean.panel.models.panel.SpatialUnitPanel;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.dashboard.DashboardModel;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
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
    private final PersonService personService;
    private final ConceptService conceptService;
    private final StratigraphicRelationshipService stratigraphicRelationshipService;
    private final BreadcrumbBean breadcrumbBean;
    private DashboardModel responsiveModel;
    private static final String RESPONSIVE_CLASS = "col-12 lg:col-6 xl:col-6";

    @Getter
    private List<AbstractPanel> panels = new ArrayList<>();
    private AbstractPanel fullscreenPanel;


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

    @PostConstruct
    public void init()  {

        // Below is just for testing

        //panels.add(new ActionUnitPanel(actionUnitService,langui.bean, sessionSettings, fieldConfigurationService, fieldService));

    }

    public void addSpatialUnitListPanel() {
        panels.add(panelFactory.createSpatialUnitListPanel());
    }

    public void goToHomeCurrentPanel(AbstractPanel panel) {
        // Change current panel type add item to its breadcrumb
        int index = panels.indexOf(panel);
        SpatialUnitListPanel newPanel = panelFactory.createSpatialUnitListPanel();
        panels.set(index,newPanel);
    }

    public void goToSpatialUnitByIdNewPanel(Long id, AbstractPanel currentPanel) {
        // Create new panel type and add items to its breadcrumb
        SpatialUnitPanel newPanel = panelFactory.createSpatialUnitPanel(id, currentPanel.getBreadcrumb());
        panels.add(0,newPanel);
    }

    public void goToSpatialUnitByIdCurrentPanel(Long id, AbstractPanel currentPanel) {
        // Change current panel type add item to its breadcrumb
        int index = panels.indexOf(currentPanel);
        SpatialUnitPanel newPanel = panelFactory.createSpatialUnitPanel(id, currentPanel.getBreadcrumb());
        panels.set(index,newPanel);
    }

    public void fullScreen(AbstractPanel panel) {
        // Could use setter if we don't add more code
        fullscreenPanel = panel;
    }

    public void closeFullScreen() {
        fullscreenPanel = null;
    }

//    public void addNewRecordingUnitPanel(ActionUnit parent) {
//
//        panels.add(0,new NewRecordingUnitPanel(recordingUnitService, actionUnitService, historyService, personService, fieldService, langui.bean, conceptService,
//                sessionSettings, fieldConfigurationService, stratigraphicRelationshipService, parent));
//    }
}