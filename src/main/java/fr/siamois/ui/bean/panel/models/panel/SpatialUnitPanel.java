package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.domain.services.form.CustomFieldService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.utils.DocumentUtils;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.DocumentCreationBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.panel.utils.DataLoaderUtils;
import fr.siamois.ui.bean.panel.utils.SpatialUnitHelperService;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import software.xdev.chartjs.model.charts.BarChart;
import software.xdev.chartjs.model.color.RGBAColor;
import software.xdev.chartjs.model.data.BarData;
import software.xdev.chartjs.model.dataset.BarDataset;
import software.xdev.chartjs.model.options.BarOptions;
import software.xdev.chartjs.model.options.Plugins;
import software.xdev.chartjs.model.options.Title;
import software.xdev.chartjs.model.options.Tooltip;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>This bean handles the spatial unit page</p>
 *
 * @author Grégory Bliault
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SpatialUnitPanel extends AbstractPanel {

    // Dependencies
    private final SpatialUnitService spatialUnitService;
    private final RecordingUnitService recordingUnitService;
    private final ActionUnitService actionUnitService;
    private final SessionSettingsBean sessionSettings;
    private final SpatialUnitHelperService spatialUnitHelperService;
    private final DocumentService documentService;
    private final DocumentCreationBean documentCreationBean;
    private final CustomFieldService customFieldService;


    // Locals
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

    private List<CustomField> availableFields;
    private List<CustomField> selectedFields;

    private String barModel;

    private Long idunit;  // ID of the spatial unit

    private List<Document> documents;


    private SpatialUnitPanel(SpatialUnitService spatialUnitService, RecordingUnitService recordingUnitService, ActionUnitService actionUnitService, SessionSettingsBean sessionSettings, SpatialUnitHelperService spatialUnitHelperService, DocumentService documentService, DocumentCreationBean documentCreationBean, CustomFieldService customFieldService) {
        super("Unité spatiale", "bi bi-geo-alt", "siamois-panel spatial-unit-panel spatial-unit-single-panel");
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.sessionSettings = sessionSettings;
        this.spatialUnitHelperService = spatialUnitHelperService;
        this.documentService = documentService;
        this.documentCreationBean = documentCreationBean;
        this.customFieldService = customFieldService;
    }

    @Override
    public String display() {
        return "/panel/spatialUnitPanel.xhtml";
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

        spatialUnitHelperService.reinitialize(
                unit -> this.spatialUnit = unit,
                msg -> this.spatialUnitErrorMessage = msg,
                msg -> this.spatialUnitListErrorMessage = msg,
                msg -> this.recordingUnitListErrorMessage = msg,
                msg -> this.actionUnitListErrorMessage = msg,
                list -> this.spatialUnitList = list,
                list -> this.recordingUnitList = list,
                list -> this.actionUnitList = list,
                list -> this.spatialUnitParentsList = list,
                msg -> this.spatialUnitParentsListErrorMessage = msg
        );

        if (idunit == null) {
            this.spatialUnitErrorMessage = "The ID of the spatial unit must be defined";
            return;
        }

        try {
            this.spatialUnit = spatialUnitService.findById(idunit);
            availableFields = customFieldService.findAllFieldsBySpatialUnitId(idunit);
            selectedFields = new ArrayList<>();

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
        documents = documentService.findForSpatialUnit(spatialUnit);
    }

    public void visualise(SpatialUnitHist history) {
        spatialUnitHelperService.visualise(history, hist -> this.revisionToDisplay = hist);
    }

    public void restore(SpatialUnitHist history) {
        spatialUnitHelperService.restore(history);
    }

    public String getFormattedValue(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof Number) {
            // Integer or Number case
            return value.toString();
        } else if (value instanceof List<?> list) {
            // Handle list of concepts

            return list.stream()
                    .map(item -> (item instanceof Concept concept) ? concept.getLabel() : item.toString())
                    .collect(Collectors.joining(", "));
        }

        return value.toString(); // Default case
    }

    public StreamedContent streamOf(Document document) {
        return DocumentUtils.streamOf(documentService , document);
    }

    public void saveDocument() {
        try {
            BufferedInputStream currentFile = new BufferedInputStream(documentCreationBean.getDocFile().getInputStream());
            String hash = documentService.getMD5Sum(currentFile);
            currentFile.mark(Integer.MAX_VALUE);
            if (documentService.existInSpatialUnitByHash(spatialUnit, hash)) {
                log.error("Document already exists in spatial unit");
                currentFile.reset();
                return;
            }
        } catch (IOException e) {
            log.error("Error while processing spatial unit document", e);
            return;
        }

        Document created = documentCreationBean.createDocument();
        if (created == null)
            return;
        log.trace("Document created: {}", created);
        documentService.addToSpatialUnit(created, spatialUnit);
        log.trace("Document added to spatial unit: {}", spatialUnit);
        PrimeFaces.current().executeScript("PF('newDocumentDiag').hide()");
        PrimeFaces.current().ajax().update("spatialUnitFormTabs:suDocumentsTab");
    }

    public boolean contentIsImage(String mimeType) {
        MimeType currentMimeType = MimeType.valueOf(mimeType);
        return currentMimeType.getType().equals("image");
    }

    public void initDialog() {
        log.trace("initDialog");
        documentCreationBean.init();
        documentCreationBean.setActionOnSave(this::saveDocument);
        PrimeFaces.current().executeScript("PF('newDocumentDiag').show()");
    }

    public static class SpatialUnitPanelBuilder {

        private final SpatialUnitPanel spatialUnitPanel;

        public SpatialUnitPanelBuilder(ObjectProvider<SpatialUnitPanel> spatialUnitPanelProvider) {
            this.spatialUnitPanel = spatialUnitPanelProvider.getObject();
        }

        public SpatialUnitPanelBuilder id(Long id) {
            spatialUnitPanel.setIdunit(id);
            return this;
        }

        public SpatialUnitPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            spatialUnitPanel.setBreadcrumb(breadcrumb);

            return this;
        }

        public SpatialUnitPanel build() {
            spatialUnitPanel.init();
            return spatialUnitPanel;
        }
    }

}