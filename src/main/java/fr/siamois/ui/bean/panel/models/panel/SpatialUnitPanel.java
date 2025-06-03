package fr.siamois.ui.bean.panel.models.panel;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectOneFromFieldCode;
import fr.siamois.domain.models.form.customfield.CustomFieldText;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswer;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerSelectOneFromFieldCode;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerText;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.models.vocabulary.label.ConceptLabel;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.domain.services.form.CustomFieldService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.LabelService;
import fr.siamois.domain.utils.DateUtils;
import fr.siamois.domain.utils.DocumentUtils;
import fr.siamois.domain.utils.MessageUtils;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.clone.SpatialUnitClone;
import fr.siamois.ui.bean.dialog.document.DocumentCreationBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.bean.panel.utils.DataLoaderUtils;
import fr.siamois.ui.bean.panel.utils.SpatialUnitHelperService;
import fr.siamois.ui.lazydatamodel.ActionUnitInSpatialUnitLazyDataModel;
import fr.siamois.ui.lazydatamodel.SpatialUnitChildrenLazyDataModel;
import fr.siamois.ui.lazydatamodel.SpatialUnitParentsLazyDataModel;
import jakarta.annotation.PostConstruct;
import jakarta.faces.component.UIComponent;
import jakarta.faces.event.AjaxBehaviorEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class SpatialUnitPanel extends AbstractPanel implements Serializable {

    // Dependencies
    private final transient  SpatialUnitService spatialUnitService;
    private final transient RecordingUnitService recordingUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient SessionSettingsBean sessionSettings;
    private final transient SpatialUnitHelperService spatialUnitHelperService;
    private final transient DocumentService documentService;
    private final transient DocumentCreationBean documentCreationBean;
    private final transient CustomFieldService customFieldService;
    private final transient ConceptService conceptService;
    private final transient LabelService labelService;
    private final transient LangBean langBean;
    private final transient PersonService personService;

    // Locals
    private transient SpatialUnit spatialUnit;
    private Boolean hasUnsavedModifications ; // Did we modify the spatial unit?
    private int activeTabIndex ; // Keeping state of active tab
    private SpatialUnitClone backupClone;


    private String spatialUnitErrorMessage;
    private transient List<SpatialUnit> spatialUnitList;
    private transient List<SpatialUnit> spatialUnitParentsList;
    private transient List<RecordingUnit> recordingUnitList;
    private transient List<ActionUnit> actionUnitList;
    private String spatialUnitListErrorMessage;
    private String spatialUnitParentsListErrorMessage;
    private String actionUnitListErrorMessage;
    private String recordingUnitListErrorMessage;


    private transient List<SpatialUnitHist> historyVersion;
    private transient SpatialUnitHist revisionToDisplay = null;

    private transient List<CustomField> availableFields;
    private transient List<CustomField> selectedFields;

    // lazy model for children
    private long totalChildrenCount = 0;
    private List<Concept> selectedCategoriesChildren;
    private SpatialUnitChildrenLazyDataModel lazyDataModelChildren ;
    // lazy model for parents
    private long totalParentsCount = 0;
    private List<Concept> selectedCategoriesParents;
    private SpatialUnitParentsLazyDataModel lazyDataModelParents ;
    // Lazy model for actions in the spatial unit
    private ActionUnitInSpatialUnitLazyDataModel actionLazyDataModel;
    private long totalActionUnitCount;


    private String barModel;

    private Long idunit;  // ID of the spatial unit

    private List<Document> documents;

    // Gestion du formulaire via form layout
    private List<CustomFormPanel> layout ; // details tab form
    private List<CustomFormPanel> overviewLayout ; // overview tab form
    private CustomFieldText nameField;
    private CustomFieldSelectOneFromFieldCode typeField;
    private CustomFormResponse formResponse ; // answers to all the fields from overview and details
    private Vocabulary systemTheso ;
    private Concept nameConcept ;
    private Concept spatialUnitTypeConcept;






    @Autowired
    private SpatialUnitPanel(SpatialUnitService spatialUnitService, RecordingUnitService recordingUnitService, ActionUnitService actionUnitService, SessionSettingsBean sessionSettings, SpatialUnitHelperService spatialUnitHelperService, DocumentService documentService, DocumentCreationBean documentCreationBean, CustomFieldService customFieldService,
                             ConceptService conceptService, LabelService labelService, LangBean langBean, PersonService personService) {

        super("common.entity.spatialUnit", "bi bi-geo-alt", "siamois-panel spatial-unit-panel spatial-unit-single-panel");
        this.spatialUnitService = spatialUnitService;
        this.recordingUnitService = recordingUnitService;
        this.actionUnitService = actionUnitService;
        this.sessionSettings = sessionSettings;
        this.spatialUnitHelperService = spatialUnitHelperService;
        this.documentService = documentService;
        this.documentCreationBean = documentCreationBean;
        this.customFieldService = customFieldService;
        this.labelService = labelService;
        this.conceptService = conceptService;
        this.langBean = langBean;
        this.personService = personService;
    }


    public List<ConceptLabel> categoriesAvailable() {
        List<Concept> cList = conceptService.findAllBySpatialUnitOfInstitution(sessionSettings.getSelectedInstitution());
        return cList.stream()
                .map(concept -> labelService.findLabelOf(
                        concept, langBean.getLanguageCode()
                ))
                .toList();

    }

    public void onTabChange(TabChangeEvent event) {
        // update tab inddex
        TabView tabView = (TabView) event.getComponent(); // Get the TabView
        Tab activeTab = event.getTab(); // Get the selected tab

        int index = activeTabIndex;
        List<Tab> tabs = tabView.getChildren().stream()
                .filter(child -> child instanceof Tab)
                .map(child -> (Tab) child)
                .toList();

        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).equals(activeTab)) {
                index = i;
                break;
            }
        }

        activeTabIndex = index;
    }

    @Override
    public String display() {
        return "/panel/spatialUnitPanel.xhtml";
    }

    @Override
    public String ressourceUri() {
        return "/spatialunit/" + idunit;
    }

    @Override
    public String displayHeader() {
        return "/panel/header/spatialUnitPanelHeader.xhtml";
    }

    public void setFieldAnswerHasBeenModified(CustomField field) {

        formResponse.getAnswers().get(field).setHasBeenModified(true);
        hasUnsavedModifications = true;

    }

    public void setFieldConceptAnswerHasBeenModified(AjaxBehaviorEvent event) {
        UIComponent component = event.getComponent();
        CustomField field = (CustomField) component.getAttributes().get("field");

        formResponse.getAnswers().get(field).setHasBeenModified(true);
        hasUnsavedModifications = true;
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

    public List<Person> authorsAvailable() {

        return personService.findAllAuthorsOfSpatialUnitByInstitution(sessionSettings.getSelectedInstitution());

    }

    public void initForms() {

        // Get from from DB in futur iteration

        // Init details tab form
        layout = new ArrayList<>();
        CustomFormPanel mainPanel = new CustomFormPanel();
        mainPanel.setIsSystemPanel(true);
        mainPanel.setName("common.header.general");
        // One row
        CustomRow row1 = new CustomRow();
        // Two cols

        CustomCol col1 = new CustomCol();
        nameField = new CustomFieldText();
        nameField.setIsSystemField(true);
        nameField.setLabel("spatialunit.field.name");
        col1.setField(nameField);
        col1.setClassName("ui-g-12 ui-md-6 ui-lg-4");

        CustomCol col2 = new CustomCol();
        typeField = new CustomFieldSelectOneFromFieldCode();
        typeField.setLabel("spatialunit.field.type");
        typeField.setIsSystemField(true);
        typeField.setFieldCode(SpatialUnit.CATEGORY_FIELD_CODE);
        col2.setField(typeField);
        col2.setClassName("ui-g-12 ui-md-6 ui-lg-4");

        row1.setColumns(List.of(col1, col2));
        mainPanel.setRows(List.of(row1));
        layout.add(mainPanel);

        // init overveiw tab form
        overviewLayout = new ArrayList<>();
        CustomFormPanel mainOverviewPanel = new CustomFormPanel();
        mainOverviewPanel.setIsSystemPanel(true);
        mainOverviewPanel.setName("common.header.general");
        // One row
        CustomRow row2 = new CustomRow();
        // one cols
        CustomCol col3 = new CustomCol();
        col3.setField(typeField);
        col3.setClassName("ui-g-12 ui-md-6 ui-lg-4");
        row2.setColumns(List.of(col3));
        mainOverviewPanel.setRows(List.of(row2));
        overviewLayout.add(mainOverviewPanel);

        // Init form answers
        formResponse = new CustomFormResponse();
        Map<CustomField, CustomFieldAnswer> answers = new HashMap<>();
        CustomFieldAnswerText nameAnswer = new CustomFieldAnswerText();
        CustomFieldAnswerSelectOneFromFieldCode typeAnswer = new CustomFieldAnswerSelectOneFromFieldCode();
        nameAnswer.setValue(spatialUnit.getName());
        nameAnswer.setHasBeenModified(false);
        answers.put(nameField, nameAnswer);
        typeAnswer.setValue(spatialUnit.getCategory());
        typeAnswer.setHasBeenModified(false);
        answers.put(typeField, typeAnswer);
        formResponse.setAnswers(answers);
    }

    public void refreshUnit() {

        hasUnsavedModifications = false;
        spatialUnit = null;
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

        try {

            this.spatialUnit = spatialUnitService.findById(idunit);
            this.setTitleCodeOrTitle(spatialUnit.getName()); // Set panel title

            backupClone = new SpatialUnitClone(spatialUnit);

            initForms();

            // Get direct parents and children counts

            // Fields for recording unit table
            //availableFields = customFieldService.findAllFieldsBySpatialUnitId(idunit);
            selectedFields = new ArrayList<>();

            // Get all the CHILDREN of the spatial unit
            selectedCategoriesChildren = new ArrayList<>();
            lazyDataModelChildren= new SpatialUnitChildrenLazyDataModel(
                    spatialUnitService,
                    langBean,
                    spatialUnit
            );
            totalChildrenCount = spatialUnitService.countChildrenByParentId(spatialUnit.getId());

            // Get all the Parents of the spatial unit
            selectedCategoriesParents = new ArrayList<>();
            lazyDataModelParents = new SpatialUnitParentsLazyDataModel(
                    spatialUnitService,
                    langBean,
                    spatialUnit
            );
            totalParentsCount = spatialUnitService.countParentsByChildId(spatialUnit.getId());

            // Action in spatial unit lazy model
            actionLazyDataModel = new ActionUnitInSpatialUnitLazyDataModel(
                    actionUnitService,
                    sessionSettings,
                    langBean,
                    spatialUnit
            );
            totalActionUnitCount = actionUnitService.countBySpatialContext(spatialUnit);

            // add to BC
            this.getBreadcrumb().addSpatialUnit(spatialUnit);

        } catch (RuntimeException e) {
            this.spatialUnitErrorMessage = "Failed to load spatial unit: " + e.getMessage();
        }

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

    public void cancelChanges() {
        spatialUnit.setGeom(backupClone.getGeom());
        spatialUnit.setName(backupClone.getName());
        spatialUnit.setValidated(backupClone.getValidated());
        spatialUnit.setArk(backupClone.getArk());
        spatialUnit.setCategory(backupClone.getCategory());
        hasUnsavedModifications = false;
        initForms();
    }


    public void init() {

        createBarModel();

        activeTabIndex = 0;

        systemTheso = new Vocabulary();
        systemTheso.setBaseUri("https://siamois.fr");
        systemTheso.setExternalVocabularyId("SYSTEM");
        nameConcept = new Concept();
        nameConcept.setExternalId("SYSTEM_NAME");
        nameConcept.setVocabulary(systemTheso);


        if (idunit == null) {
            this.spatialUnitErrorMessage = "The ID of the spatial unit must be defined";
            return;
        }

        refreshUnit();



        if (this.spatialUnit == null) {
            this.spatialUnitErrorMessage = "The ID of the spatial unit must be defined";
            return;
        }

        this.setTitle(spatialUnit.getName()); // Set panel title
        // add to BC
        this.getBreadcrumb().addSpatialUnit(spatialUnit);


    }

    public void visualise(SpatialUnitHist history) {
        spatialUnitHelperService.visualise(history, hist -> this.revisionToDisplay = hist);
    }

    public void restore(SpatialUnitHist history) {
        spatialUnitHelperService.restore(history);
        // refresh panel
        init();
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", history.getName());
    }

    public String formatDate(OffsetDateTime offsetDateTime) {
        return DateUtils.formatOffsetDateTime(offsetDateTime);
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
            String langCode = sessionSettings.getLanguageCode();
            return list.stream()
                    .map(item -> (item instanceof Concept concept) ? labelService.findLabelOf(concept, langCode).getValue() : item.toString())
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

        documents.add(created);
        PrimeFaces.current().executeScript("PF('newDocumentDiag').hide()");
        PrimeFaces.current().ajax().update("spatialUnitForm");
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

    public Boolean isHierarchyTabEmpty () {
        return (totalChildrenCount + totalParentsCount) == 0;
    }

    public void save(Boolean validated) {

        // Recupération des champs systeme

        // Name
        CustomFieldAnswerText nameAnswer = (CustomFieldAnswerText) formResponse.getAnswers().get(nameField);
        CustomFieldAnswerSelectOneFromFieldCode typeAnswer = (CustomFieldAnswerSelectOneFromFieldCode) formResponse.getAnswers().get(typeField);
        spatialUnit.setName(nameAnswer.getValue());
        spatialUnit.setCategory(typeAnswer.getValue());

        spatialUnit.setValidated(validated);
        try {
            spatialUnitService.save(spatialUnit);
        }
        catch(FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.spatialUnits.updateFailed", spatialUnit.getName());
            return ;
        }

        refreshUnit();
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", spatialUnit.getName());
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