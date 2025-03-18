package fr.siamois.ui.bean.spatialunit;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.domain.utils.DocumentUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.RedirectBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.DocumentCreationBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.model.StreamedContent;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.web.context.annotation.SessionScope;
import software.xdev.chartjs.model.charts.BarChart;
import software.xdev.chartjs.model.color.RGBAColor;
import software.xdev.chartjs.model.data.BarData;
import software.xdev.chartjs.model.dataset.BarDataset;
import software.xdev.chartjs.model.options.BarOptions;
import software.xdev.chartjs.model.options.Plugins;
import software.xdev.chartjs.model.options.Title;
import software.xdev.chartjs.model.options.Tooltip;

import java.io.IOException;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * <p>This bean handles the spatial unit page</p>
 *
 * @author Grégory Bliault
 */
@Slf4j
@Component
@SessionScope
@Data
public class SpatialUnitBean implements Serializable {

    private final transient SpatialUnitService spatialUnitService;
    private final transient RecordingUnitService recordingUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient HistoryService historyService;
    private final SessionSettingsBean sessionSettingsBean;
    private final RedirectBean redirectBean;
    private final LangBean langBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient DocumentService documentService;
    private final DocumentCreationBean documentCreationBean;

    private SpatialUnit spatialUnit;
    private String spatialUnitErrorMessage;
    private transient List<SpatialUnit> spatialUnitList;
    private transient List<SpatialUnit> spatialUnitParentsList;
    private transient List<RecordingUnit> recordingUnitList;
    private transient List<ActionUnit> actionUnitList;
    private String spatialUnitListErrorMessage;
    private String spatialUnitParentsListErrorMessage;
    private String actionUnitListErrorMessage;
    private String recordingUnitListErrorMessage;
    private transient List<Document> documents;

    private transient List<SpatialUnitHist> historyVersion;
    private SpatialUnitHist revisionToDisplay = null;

    private String barModel;

    private Long id;  // ID of the spatial unit

    public SpatialUnitBean(SpatialUnitService spatialUnitService,
                           RecordingUnitService recordingUnitService,
                           ActionUnitService actionUnitService,
                           HistoryService historyService,
                           SessionSettingsBean sessionSettingsBean, RedirectBean redirectBean, LangBean langBean, FieldConfigurationService fieldConfigurationService, DocumentService documentService, DocumentCreationBean documentCreationBean) {
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.historyService = historyService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.redirectBean = redirectBean;
        this.langBean = langBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.documentService = documentService;
        this.documentCreationBean = documentCreationBean;
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


    public void createBarModel() {
        barModel = new BarChart()
                .setData(new BarData()
                        .addDataset(new BarDataset()
                                .setData(65, 59, 80)
                                .setBackgroundColor(List.of(new RGBAColor(255, 99, 132, 0.5), new RGBAColor(12, 99, 132, 0.5), new RGBAColor(255, 17, 51, 0.5)))
                                .setBorderColor(new RGBAColor(255, 99, 132, 1))
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

    public void init() throws IOException {

        createBarModel();

        reinitializeBean();

        if (id == null) {
            log.error("The Spatial Unit page should not be accessed without ID or by direct page path");
            redirectBean.redirectTo(HttpStatus.NOT_FOUND);
            return;
        }

        try {
            this.spatialUnit = spatialUnitService.findById(id);
        } catch (RuntimeException e) {
            log.error("Failed to find spatial unit with id {}", id, e);
            this.spatialUnitErrorMessage = "Failed to load spatial unit: " + e.getMessage();
        }

        if (this.spatialUnit == null) {
            log.error("Spatial unit is null");
            this.spatialUnitErrorMessage = "The spatial unit could not be found";
            redirectBean.redirectTo(HttpStatus.NOT_FOUND);
            return;
        }

        try {
            this.spatialUnitListErrorMessage = null;
            this.spatialUnitList = spatialUnitService.findAllChildOfSpatialUnit(spatialUnit);
        } catch (RuntimeException e) {
            this.spatialUnitList = null;
            this.spatialUnitListErrorMessage = "Unable to load spatial units: " + e.getMessage();
        }
        try {
            this.spatialUnitParentsListErrorMessage = null;
            this.spatialUnitParentsList = spatialUnitService.findAllParentsOfSpatialUnit(spatialUnit);
        } catch (RuntimeException e) {
            this.spatialUnitParentsList = null;
            this.spatialUnitParentsListErrorMessage = "Unable to load the parents: " + e.getMessage();
        }
        try {
            this.recordingUnitListErrorMessage = null;
            this.recordingUnitList = recordingUnitService.findAllBySpatialUnit(spatialUnit);
        } catch (RuntimeException e) {
            this.recordingUnitList = null;
            this.recordingUnitListErrorMessage = "Unable to load recording units: " + e.getMessage();
        }
        try {
            this.actionUnitListErrorMessage = null;
            this.actionUnitList = actionUnitService.findAllBySpatialUnitId(spatialUnit);
        } catch (RuntimeException e) {
            this.actionUnitList = null;
            this.actionUnitListErrorMessage = "Unable to load action units: " + e.getMessage();
        }

        this.documents = documentService.findForSpatialUnit(spatialUnit);

        historyVersion = historyService.findSpatialUnitHistory(spatialUnit);
    }

    public String formatDate(OffsetDateTime offsetDateTime) {
        return DateUtils.formatOffsetDateTime(offsetDateTime);
    }

    public void visualise(SpatialUnitHist history) {
        log.trace("History version changed to {}", history.toString());
        revisionToDisplay = history;
    }

    public void restore(SpatialUnitHist history) {
        log.trace("Restore order received");
        spatialUnitService.restore(history);
        PrimeFaces.current().executeScript("PF('restored-dlg').show()");
    }

    public StreamedContent streamOf(Document document) {
        return DocumentUtils.streamOf(documentService , document);
    }

    public void saveDocument() {
        Document created = documentCreationBean.createDocument();
        if (created == null)
            return;
        documentService.addToSpatialUnit(created, spatialUnit);
        PrimeFaces.current().executeScript("PF('newDocumentDiag').hide()");
        PrimeFaces.current().ajax().update("spatialUnitFormTabs:suDocumentsTab");
    }

    public boolean contentIsImage(String mimeType) {
        MimeType currentMimeType = MimeType.valueOf(mimeType);
        return currentMimeType.getType().equals("image");
    }

}
