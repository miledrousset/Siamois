package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.ui.bean.panel.utils.DataLoaderUtils;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.panel.utils.SpatialUnitHelperService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import software.xdev.chartjs.model.charts.BarChart;
import software.xdev.chartjs.model.color.RGBAColor;
import software.xdev.chartjs.model.data.BarData;
import software.xdev.chartjs.model.dataset.BarDataset;
import software.xdev.chartjs.model.options.BarOptions;
import software.xdev.chartjs.model.options.Plugins;
import software.xdev.chartjs.model.options.Title;
import software.xdev.chartjs.model.options.Tooltip;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This bean handles the spatial unit page</p>
 *
 * @author Grégory Bliault
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class SpatialUnitPanel extends AbstractPanel {

    private final SpatialUnitService spatialUnitService;
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;
    private final SessionSettingsBean sessionSettings;
    private final SpatialUnitHelperService spatialUnitHelperService;

    private SpatialUnit spatialUnit;
    private String spatialUnitErrorMessage;
    private List<SpatialUnit> spatialUnitList;
    private List<SpatialUnit> spatialUnitParentsList;
    private List<RecordingUnit> recordingUnitList;
    private List<ActionUnit> actionUnitList;
    private String spatialUnitListErrorMessage;
    private String spatialUnitParentsListErrorMessage;
    private String actionUnitListErrorMessage;
    private String recordingUnitListErrorMessage;

    private List<SpatialUnitHist> historyVersion;
    private SpatialUnitHist revisionToDisplay = null;

    private String barModel;

    private Long idunit;  // ID of the spatial unit

    public SpatialUnitPanel(SpatialUnitService spatialUnitService, RecordingUnitService recordingUnitService, ActionUnitService actionUnitService, SessionSettingsBean sessionSettings, Long id, PanelBreadcrumb currentBreadcrumb, SpatialUnitHelperService spatialUnitHelperService) {
        super("spatial", "Unité spatiale", "spatial", "pi pi-map-marker");
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.sessionSettings = sessionSettings;
        this.idunit = id;
        this.spatialUnitHelperService = spatialUnitHelperService;
        this.setBreadcrumb(new PanelBreadcrumb());
        this.getBreadcrumb().getModel().getElements().clear();
        this.getBreadcrumb().getModel().getElements().addAll(new ArrayList<>(currentBreadcrumb.getModel().getElements()));
        init();
    }

    @Override
    public String display() {
        return "/panel/spatialUnitPanel.xhtml";
    }


    public void reinitializeBean() {
        this.spatialUnit = null;
        this.spatialUnitErrorMessage = null;
        this.spatialUnitListErrorMessage = null;
        this.recordingUnitListErrorMessage = null;
        this.actionUnitListErrorMessage = null;
        this.spatialUnitList = null;
        this.recordingUnitList = null;
        this.actionUnitList = null;
        this.spatialUnitParentsList = null;
        this.spatialUnitParentsListErrorMessage = null;
    }

    public String goToSpatialUnitById(Long id) {
        log.trace("go to spatial unit");
        return "/pages/spatialUnit/spatialUnit.xhtml?id=" + id + "&faces-redirect=true";
    }


    public void createBarModel() {
        barModel = new BarChart()
                .setData(new BarData()
                        .addDataset(new BarDataset()
                                .setData(65, 59, 80)
                                .setBackgroundColor(List.of(new RGBAColor(255, 99, 132, 0.5),new RGBAColor(12, 99, 132, 0.5),new RGBAColor(255, 17, 51, 0.5)))
                                .setBorderColor(new RGBAColor(255, 99, 132,1))
                                .setBorderWidth(1))
                        .setLabels("Hors contexte", "Unité stratigraphique", "Unité construite"))
                .setOptions(new BarOptions()
                        .setResponsive(true)
                        .setMaintainAspectRatio(false)
                        .setPlugins(new Plugins()
                                .setTooltip(new Tooltip().setMode("index"))
                                .setTitle(new Title()
                                        .setDisplay(true)
                                        .setText("Unités d'enregistrement (mockup)")
                                )
                        )
                ).toJson();
    }


    @PostConstruct
    public void init() {

        createBarModel();

        reinitializeBean();

        if (idunit == null) {
            this.spatialUnitErrorMessage = "The ID of the spatial unit must be defined";
            return;
        }

        try {
            this.spatialUnit = spatialUnitService.findById(idunit);
            this.setTitle(spatialUnit.getName()); // Set panel title
            // add to BC
            this.getBreadcrumb().addSpatialUnit(spatialUnit);
        } catch (RuntimeException e) {
            this.spatialUnitErrorMessage = "Failed to load spatial unit: " + e.getMessage();
        }

        if (this.spatialUnit == null) {
            this.spatialUnitErrorMessage = "The ID of the spatial unit must be defined";
            return;
        }

        DataLoaderUtils.loadData(
                () -> spatialUnitService.findAllChildOfSpatialUnit(spatialUnit),
                list -> this.spatialUnitList = list,
                msg -> this.spatialUnitListErrorMessage = msg,
                "Unable to load spatial units: "
        );

        DataLoaderUtils.loadData(
                () -> spatialUnitService.findAllParentsOfSpatialUnit(spatialUnit),
                list -> this.spatialUnitParentsList = list,
                msg -> this.spatialUnitParentsListErrorMessage = msg,
                "Unable to load the parents: "
        );

        DataLoaderUtils.loadData(
                () -> recordingUnitService.findAllBySpatialUnit(spatialUnit),
                list -> this.recordingUnitList = list,
                msg -> this.recordingUnitListErrorMessage = msg,
                "Unable to load recording units: "
        );

        DataLoaderUtils.loadData(
                () -> actionUnitService.findAllBySpatialUnitId(spatialUnit),
                list -> this.actionUnitList = list,
                msg -> this.actionUnitListErrorMessage = msg,
                "Unable to load action units: "
        );

        historyVersion = spatialUnitHelperService.findHistory(spatialUnit);
    }

    public void visualise(SpatialUnitHist history) {
        spatialUnitHelperService.visualise(history, hist -> this.revisionToDisplay = hist);
    }

    public void restore(SpatialUnitHist history) {
        spatialUnitHelperService.restore(history);
    }


}