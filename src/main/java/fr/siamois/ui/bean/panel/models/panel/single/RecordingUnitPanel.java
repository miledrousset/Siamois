package fr.siamois.ui.bean.panel.models.panel.single;

import fr.siamois.domain.models.actionunit.ActionUnitFormMapping;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.actionunit.ActionUnitNotFoundException;
import fr.siamois.domain.models.form.customfield.*;
import fr.siamois.domain.models.form.customfieldanswer.*;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.history.RecordingUnitHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.models.exceptions.ErrorProcessingExpansionException;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.RedirectBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.document.DocumentCreationBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.menu.DefaultMenuItem;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RecordingUnitPanel extends AbstractSingleEntityPanel<RecordingUnit, RecordingUnitHist> {

    // Deps
    protected final LangBean langBean;
    protected final SessionSettingsBean sessionSettingsBean;
    protected final transient SpatialUnitService spatialUnitService;
    protected final transient ActionUnitService actionUnitService;
    protected final transient RecordingUnitService recordingUnitService;
    protected final transient PersonService personService;
    private final RedirectBean redirectBean;
    private final transient HistoryService historyService;
    private final transient DocumentService documentService;
    protected final transient ConceptService conceptService;
    protected final transient FieldConfigurationService fieldConfigurationService;
    protected final FlowBean flowBean;

    // ---------- Locals
    // RU
    protected RecordingUnit recordingUnit;

    // Form
    protected transient CustomForm additionalForm;

    // form
    private CustomFieldText idField;
    private Concept idConcept;
    private CustomFieldSelectOneFromFieldCode typeField;
    private Concept actionUnitTypeConcept;

    protected RecordingUnitPanel(LangBean langBean,
                                 SessionSettingsBean sessionSettingsBean,
                                 SpatialUnitService spatialUnitService,
                                 ActionUnitService actionUnitService,
                                 RecordingUnitService recordingUnitService,
                                 PersonService personService, ConceptService conceptService,
                                 FieldConfigurationService fieldConfigurationService,
                                 FlowBean flowBean,
                                 DocumentCreationBean documentCreationBean,
                                 RedirectBean redirectBean,
                                 HistoryService historyService,
                                 DocumentService documentService) {

        super("common.entity.recordingunit",
                "bi bi-pencil-square",
                "siamois-panel recording-unit-panel recording-unit-single-panel",
                documentCreationBean);
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
        this.recordingUnitService = recordingUnitService;
        this.personService = personService;
        this.conceptService = conceptService;
        this.fieldConfigurationService = fieldConfigurationService;
        this.flowBean = flowBean;
        this.redirectBean = redirectBean;
        this.historyService = historyService;
        this.documentService = documentService;
    }

    @Override
    public String display() {
        return "/panel/recordingUnitPanel.xhtml";
    }

    @Override
    public String ressourceUri() {
        return "/recording-unit/" + idunit;
    }

    @Override
    public String displayHeader() {
        return "/panel/header/recordingUnitPanelHeader.xhtml";
    }

    public LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDT) {
        return offsetDT.toLocalDate();
    }

    public List<Concept> fetchChildrenOfConcept(Concept concept) {
        List<Concept> concepts ;

        try {
            concepts = conceptService.findDirectSubConceptOf(concept);
        } catch (ErrorProcessingExpansionException e) {
            log.error(e.getMessage());
            log.debug(e.getMessage(), e);
            return new ArrayList<>();
        }

        return concepts;

    }

    public void initializeAnswer(CustomField field) {
        if (recordingUnit.getFormResponse().getAnswers().get(field) == null) {
            // Init missing parameters
            if (field instanceof CustomFieldSelectMultiple) {
                recordingUnit.getFormResponse().getAnswers().put(field, new CustomFieldAnswerSelectMultiple());
            } else if (field instanceof CustomFieldInteger) {
                recordingUnit.getFormResponse().getAnswers().put(field, new CustomFieldAnswerInteger());
            }
        }
    }

    public void changeCustomForm() {
        // Do we have a form available for the selected type?
        Set<ActionUnitFormMapping> formsAvailable = recordingUnit.getActionUnit().getFormsAvailable();
        additionalForm = getFormForRecordingUnitType(recordingUnit.getType(), formsAvailable);
        if (recordingUnit.getFormResponse() == null) {
            recordingUnit.setFormResponse(new CustomFormResponse());
            recordingUnit.getFormResponse().setAnswers(new HashMap<>());
        }
        recordingUnit.getFormResponse().setForm(additionalForm);
        if (additionalForm != null) {
            initFormResponseAnswers();
        }


    }

    public CustomForm getFormForRecordingUnitType(Concept type, Set<ActionUnitFormMapping> availableForms) {
        return availableForms.stream()
                .filter(mapping -> mapping.getPk().getConcept().equals(type) // Vérifier le concept
                        && "RECORDING_UNIT".equals(mapping.getPk().getTableName())) // Vérifier le tableName
                .map(mapping -> mapping.getPk().getForm())
                .findFirst()
                .orElse(null); // Retourner null si aucun match
    }

    public void handleSelectType() {

        if (recordingUnit.getType() != null) {

            changeCustomForm();
        } else {

        }

        recordingUnit.setSecondaryType(null);
        recordingUnit.setThirdType(null);



    }

    public void initFormResponseAnswers() {



        if (recordingUnit.getFormResponse().getForm() != null) {
            recordingUnit.getFormResponse().getForm().getLayout().stream()
                    .flatMap(section -> section.getRows().stream())      // Stream rows in each section
                    .flatMap(row -> row.getColumns().stream())           // Stream columns in each row
                    .map(CustomCol::getField)                    // Extract the field from each column
                    .forEach(this::initializeAnswer);                    // Process each field
        }


    }

    public OffsetDateTime localDateToOffsetDateTime(LocalDate localDate) {
        return localDate.atTime(LocalTime.NOON).atOffset(ZoneOffset.UTC);
    }

    @Override
    protected BaseLazyDataModel<RecordingUnit> getLazyDataModelChildren() {
        return null;
    }

    @Override
    public BaseLazyDataModel<RecordingUnit> getLazyDataModelParents() {
        return null;
    }

    public void refreshUnit() {

        // reinit
        hasUnsavedModifications = false;
        errorMessage = null;
        unit = null;

        try {

            unit = recordingUnitService.findById(idunit);
            backupClone = new RecordingUnit(unit);
            this.titleCodeOrTitle = unit.getFullIdentifier();

            initForms();

            // Get all the CHILDREN of the recording unit
            selectedCategoriesChildren = new ArrayList<>();
            totalChildrenCount = 0;
            // Get all the Parents of the recording unit
            selectedCategoriesParents = new ArrayList<>();
            totalParentsCount = 0;


        } catch (RuntimeException e) {
            this.errorMessage = "Failed to load spatial unit: " + e.getMessage();
        }


        historyVersion = historyService.findRecordingUnitHistory(unit);
        documents = documentService.findForRecordingUnit(unit);
    }

    @Override
    public void init() {
        try {
            activeTabIndex = 0;

            systemTheso = new Vocabulary();
            systemTheso.setBaseUri("https://siamois.fr");
            systemTheso.setExternalVocabularyId("SYSTEM");


            if (idunit == null) {
                this.errorMessage = "The ID of the recording unit must be defined";
                return;
            }

            refreshUnit();

            if (this.unit == null) {
                log.error("The Action Unit page should not be accessed without ID or by direct page path");
                errorMessage = "The Action Unit page should not be accessed without ID or by direct page path";
            }

            // add to BC
            DefaultMenuItem item = DefaultMenuItem.builder()
                    .value(unit.getFullIdentifier())
                    .icon("bi bi-pencil-square")
                    .build();
            this.getBreadcrumb().getModel().getElements().add(item);

        } catch (
                ActionUnitNotFoundException e) {
            log.error("Recording unit with id {} not found", idunit);
            redirectBean.redirectTo(HttpStatus.NOT_FOUND);
        } catch (
                RuntimeException e) {
            this.errorMessage = "Failed to load recording unit: " + e.getMessage();
            redirectBean.redirectTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @Override
    public List<Person> authorsAvailable() {
        return List.of();
    }

    @Override
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
        idField = new CustomFieldText();
        idField.setIsSystemField(true);
        idField.setLabel("recordingunit.field.identifier");
        col1.setField(idField);
        col1.setReadOnly(true);
        col1.setClassName(COLUMN_CLASS_NAME);

        CustomCol col2 = new CustomCol();
        typeField = new CustomFieldSelectOneFromFieldCode();
        typeField.setLabel("spatialunit.field.type");
        typeField.setIsSystemField(true);
        typeField.setFieldCode(RecordingUnit.TYPE_FIELD_CODE);
        col2.setField(typeField);
        col2.setClassName(COLUMN_CLASS_NAME);

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
        col3.setReadOnly(true);
        col3.setField(typeField);
        col3.setClassName(COLUMN_CLASS_NAME);
        row2.setColumns(List.of(col3));
        mainOverviewPanel.setRows(List.of(row2));
        overviewLayout.add(mainOverviewPanel);

        // Init form answers
        formResponse = new CustomFormResponse();
        Map<CustomField, CustomFieldAnswer> answers = new HashMap<>();
        CustomFieldAnswerText nameAnswer = new CustomFieldAnswerText();
        CustomFieldAnswerSelectOneFromFieldCode typeAnswer = new CustomFieldAnswerSelectOneFromFieldCode();
        nameAnswer.setValue(unit.getFullIdentifier());
        nameAnswer.setHasBeenModified(false);
        answers.put(idField, nameAnswer);
        typeAnswer.setValue(unit.getType());
        typeAnswer.setHasBeenModified(false);
        answers.put(typeField, typeAnswer);
        formResponse.setAnswers(answers);

    }

    @Override
    public void cancelChanges() {

    }

    @Override
    public void visualise(RecordingUnitHist history) {

    }

    @Override
    public void saveDocument() {

    }

    @Override
    public void save(Boolean validated) {

    }

    public static class RecordingUnitPanelBuilder {

        private final RecordingUnitPanel recordingUnitPanel;

        public RecordingUnitPanelBuilder(ObjectProvider<RecordingUnitPanel> recordingUnitPanelProvider) {
            this.recordingUnitPanel = recordingUnitPanelProvider.getObject();
        }

        public RecordingUnitPanel.RecordingUnitPanelBuilder id(Long id) {
            recordingUnitPanel.setIdunit(id);
            return this;
        }

        public RecordingUnitPanel.RecordingUnitPanelBuilder breadcrumb(PanelBreadcrumb breadcrumb) {
            recordingUnitPanel.setBreadcrumb(breadcrumb);

            return this;
        }


        public RecordingUnitPanel build() {
            recordingUnitPanel.init();
            return recordingUnitPanel;
        }
    }


}
