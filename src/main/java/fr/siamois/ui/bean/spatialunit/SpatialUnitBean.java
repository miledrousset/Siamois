package fr.siamois.ui.bean.spatialunit;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.exceptions.InvalidFileSizeException;
import fr.siamois.domain.models.exceptions.InvalidFileTypeException;
import fr.siamois.domain.models.exceptions.vocabulary.NoConfigForFieldException;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.domain.utils.DocumentUtils;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.RedirectBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.DocumentCreationDialog;
import jakarta.faces.application.FacesMessage;
import jakarta.servlet.ServletContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;
import org.springframework.http.HttpStatus;
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

import javax.faces.bean.SessionScoped;
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
@SessionScoped
@Data
public class SpatialUnitBean implements Serializable, DocumentCreationDialog {

    private final transient SpatialUnitService spatialUnitService;
    private final transient RecordingUnitService recordingUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient HistoryService historyService;
    private final SessionSettingsBean sessionSettingsBean;
    private final RedirectBean redirectBean;
    private final transient DocumentService documentService;
    private final LangBean langBean;
    private final transient FieldConfigurationService fieldConfigurationService;
    private final transient ServletContext servletContext;

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

    private Concept parentNature = null;
    private Concept parentScale = null;
    private Concept parentType = null;

    private String docTitle;
    private Concept docNature;
    private Concept docScale;
    private Concept docType;
    private transient UploadedFile docFile;

    private String barModel;

    private Long id;  // ID of the spatial unit

    public SpatialUnitBean(SpatialUnitService spatialUnitService,
                           RecordingUnitService recordingUnitService,
                           ActionUnitService actionUnitService,
                           HistoryService historyService,
                           SessionSettingsBean sessionSettingsBean, RedirectBean redirectBean, DocumentService documentService, LangBean langBean, FieldConfigurationService fieldConfigurationService, ServletContext servletContext) {
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.historyService = historyService;
        this.sessionSettingsBean = sessionSettingsBean;
        this.redirectBean = redirectBean;
        this.documentService = documentService;
        this.langBean = langBean;
        this.fieldConfigurationService = fieldConfigurationService;
        this.servletContext = servletContext;
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
            this.spatialUnitErrorMessage = "Failed to load spatial unit: " + e.getMessage();
        }

        if (this.spatialUnit == null) {
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

        UserInfo info = sessionSettingsBean.getUserInfo();
        this.documents = documentService.findForSpatialUnit(spatialUnit);
        try {
            parentNature = fieldConfigurationService.findConfigurationForFieldCode(info, Document.NATURE_FIELD_CODE);
            parentScale = fieldConfigurationService.findConfigurationForFieldCode(info, Document.SCALE_FIELD_CODE);
            parentType = fieldConfigurationService.findConfigurationForFieldCode(info, Document.FORMAT_FIELD_CODE);
        } catch (NoConfigForFieldException e) {
            spatialUnitErrorMessage = "No thesaurus configuration for asked fields";
        }

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

    @Override
    public void createDocument() {
        if (docFile == null) {
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "No file set");
            return;
        }

        UserInfo userInfo = sessionSettingsBean.getUserInfo();
        Document document = new Document();
        document.setTitle(getDocTitle());
        document.setNature(getDocNature());
        document.setScale(getDocScale());
        document.setFormat(getDocType());

        try {
            document = documentService.saveFile(userInfo, document, docFile.getInputStream(), servletContext.getContextPath());
            documentService.addToSpatialUnit(document, spatialUnit);
        } catch (InvalidFileTypeException e) {
            log.error("Invalid file type {}", e.getMessage());
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "This type of file is not supported");
        } catch (InvalidFileSizeException e) {
            log.error("Invalid file size {}", e.getMessage());
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "The file is too large");
        } catch (IOException e) {
            log.error("IO Exception {}", e.getMessage());
            MessageUtils.displayPlainMessage(langBean, FacesMessage.SEVERITY_ERROR, "Internal error");
        }

    }

    public String getUrlForConcept(Concept concept) {
        return fieldConfigurationService.getUrlOfConcept(concept);
    }

    public List<Concept> autocomplete(Concept parent, String input) {
        log.trace("Autocomplete order received");
        return fieldConfigurationService.fetchAutocomplete(
                sessionSettingsBean.getUserInfo(),
                parent,
                input);
    }

    public List<Concept> autocompleteNature(String input) {
        return autocomplete(parentNature, input);
    }

    public List<Concept> autocompleteScale(String input) {
        return autocomplete(parentScale, input);
    }

    public List<Concept> autocompleteType(String input) {
        return autocomplete(parentType, input);
    }

    public String regexSupportedTypes() {
        List<MimeType> supported = documentService.supportedMimeTypes();
        return DocumentUtils.allowedTypesRegex(supported);
    }

}
