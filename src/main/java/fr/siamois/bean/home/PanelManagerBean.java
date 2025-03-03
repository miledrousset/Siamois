package fr.siamois.bean.home;

import fr.siamois.bean.LangBean;
import fr.siamois.bean.SessionSettings;
import fr.siamois.bean.actionunit.ActionUnitPanel;
import fr.siamois.bean.recordingunit.NewRecordingUnitPanel;
import fr.siamois.bean.spatialunit.PanelFactory;
import fr.siamois.bean.spatialunit.SpatialUnitPanel;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.events.InstitutionChangeEvent;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.services.HistoryService;
import fr.siamois.services.PersonService;
import fr.siamois.services.SpatialUnitService;
import fr.siamois.services.actionunit.ActionUnitService;
import fr.siamois.services.recordingunit.RecordingUnitService;
import fr.siamois.services.recordingunit.StratigraphicRelationshipService;
import fr.siamois.services.vocabulary.ConceptService;
import fr.siamois.services.vocabulary.FieldConfigurationService;
import fr.siamois.services.vocabulary.FieldService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.dashboard.DashboardModel;
import org.primefaces.model.dashboard.DefaultDashboardModel;
import org.primefaces.model.dashboard.DefaultDashboardWidget;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This bean handles the home page</p>
 * <p>It is used to display the list of spatial units without parents</p>
 *
 * @author Grégory Bliault
 */
@Slf4j
@Component
@SessionScoped
@Data
public class PanelManagerBean implements Serializable {


    private final transient SpatialUnitService spatialUnitService;
    private final transient RecordingUnitService recordingUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient HistoryService historyService;
    private final transient SessionSettings sessionSettings;
    private final transient LangBean langBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient FieldService fieldService;
    private final transient PanelFactory panelFactory;
    private final PersonService personService;
    private final ConceptService conceptService;
    private final StratigraphicRelationshipService stratigraphicRelationshipService;
    private DashboardModel responsiveModel;
    private static final String RESPONSIVE_CLASS = "col-12 lg:col-6 xl:col-6";

    @Getter
    private List<AbstractPanel> panels = new ArrayList<>();


    public PanelManagerBean(SpatialUnitService spatialUnitService, RecordingUnitService recordingUnitService, ActionUnitService actionUnitService, HistoryService historyService, SessionSettings sessionSettings, LangBean langBean, FieldConfigurationService fieldConfigurationService, FieldService fieldService, PanelFactory panelFactory, PersonService personService, ConceptService conceptService, StratigraphicRelationshipService stratigraphicRelationshipService) {
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
    }

    @PostConstruct
    public void init()  {
        panels.add(panelFactory.createSpatialUnitPanel(1L));
        panels.add(new ActionUnitPanel(actionUnitService,langBean, sessionSettings, fieldConfigurationService, fieldService));

    }

    public void addNewRecordingUnitPanel(ActionUnit parent) {

        panels.add(0,new NewRecordingUnitPanel(recordingUnitService, actionUnitService, historyService, personService, fieldService, langBean, conceptService,
                sessionSettings, fieldConfigurationService, stratigraphicRelationshipService, parent));
    }
}
