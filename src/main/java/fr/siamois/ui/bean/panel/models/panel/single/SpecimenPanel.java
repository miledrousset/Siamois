package fr.siamois.ui.bean.panel.models.panel.single;

import fr.siamois.domain.models.actionunit.ActionUnitFormMapping;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.actionunit.ActionUnitNotFoundException;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.form.customfield.*;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerInteger;
import fr.siamois.domain.models.form.customfieldanswer.CustomFieldAnswerSelectMultiple;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.history.RecordingUnitHist;
import fr.siamois.domain.models.history.SpecimenHist;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.HistoryService;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.domain.services.person.PersonService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.specimen.SpecimenService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.models.exceptions.ErrorProcessingExpansionException;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.RedirectBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.dialog.document.DocumentCreationBean;
import fr.siamois.ui.bean.panel.models.PanelBreadcrumb;
import fr.siamois.ui.lazydatamodel.BaseLazyDataModel;
import fr.siamois.utils.MessageUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SpecimenPanel extends AbstractSingleEntityPanel<Specimen, SpecimenHist> {

    // Deps
    protected final transient LangBean langBean;
    protected final transient SessionSettingsBean sessionSettingsBean;
    protected final transient SpatialUnitService spatialUnitService;
    protected final transient ActionUnitService actionUnitService;
    protected final transient RecordingUnitService recordingUnitService;
    protected final transient PersonService personService;
    private final transient RedirectBean redirectBean;
    private final transient SpecimenService specimenService;
    private final transient HistoryService historyService;
    private final transient DocumentService documentService;
    protected final transient ConceptService conceptService;
    protected final transient FieldConfigurationService fieldConfigurationService;

    // ---------- Locals
    // RU
    protected RecordingUnit recordingUnit;


    // ----------- Concepts for system fields
    // Specimen identifier
    private Concept specimenIdConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4286193")
            .build();

    // Authors
    private Concept authorsConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4286246")
            .build();

    // Excavators
    private Concept collectorsConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4286247")
            .build();

    // Specimen type
    private Concept specimenTypeConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4282392")
            .build();

    // Specimen category
    private Concept specimenCategoryConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4286248")
            .build();

    // Date
    private Concept collectionDateConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4286249")
            .build();

    // --------------- Fields

    // Fields
    private CustomFieldText specimenIdField = new CustomFieldText.Builder()
            .label("recordingunit.field.identifier")
            .isSystemField(true)
            .valueBinding("fullIdentifier")
            .concept(specimenIdConcept)
            .build();

    private CustomFieldSelectMultiplePerson authorsField = new CustomFieldSelectMultiplePerson.Builder()
            .label("specimen.field.authors")
            .isSystemField(true)
            .valueBinding("authors")
            .concept(authorsConcept)
            .build();

    private CustomFieldSelectMultiplePerson collectorsField = new CustomFieldSelectMultiplePerson.Builder()
            .label("specimen.field.collectors")
            .isSystemField(true)
            .valueBinding("collectors")
            .concept(collectorsConcept)
            .build();

    private CustomFieldSelectOneFromFieldCode specimenTypeField = new CustomFieldSelectOneFromFieldCode.Builder()
            .label("specimen.field.type")
            .isSystemField(true)
            .valueBinding("type")
            .styleClass("mr-2 specimen-type-chip")
            .iconClass("bi bi-box2")
            .fieldCode(Specimen.CATEGORY_FIELD)
            .concept(specimenTypeConcept)
            .build();


    private CustomFieldDateTime collectionDateField = new CustomFieldDateTime.Builder()
            .label("specimen.field.collectionDate")
            .isSystemField(true)
            .valueBinding("collectionDate")
            .showTime(false)
            .concept(collectionDateConcept)
            .build();


    protected SpecimenPanel(LangBean langBean,
                            SessionSettingsBean sessionSettingsBean,
                            SpatialUnitService spatialUnitService,
                            ActionUnitService actionUnitService,
                            RecordingUnitService recordingUnitService,
                            PersonService personService, SpecimenService specimenService, ConceptService conceptService,
                            FieldConfigurationService fieldConfigurationService,
                            DocumentCreationBean documentCreationBean,
                            RedirectBean redirectBean,
                            HistoryService historyService,
                            DocumentService documentService) {

        super("common.entity.specimen",
                "bi bi-box2",
                "siamois-panel specimen-panel specimen-single-panel",
                documentCreationBean, sessionSettingsBean, fieldConfigurationService);
        this.langBean = langBean;
        this.sessionSettingsBean = sessionSettingsBean;
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
        this.recordingUnitService = recordingUnitService;
        this.personService = personService;
        this.specimenService = specimenService;
        this.conceptService = conceptService;
        this.fieldConfigurationService = fieldConfigurationService;
        this.redirectBean = redirectBean;
        this.historyService = historyService;
        this.documentService = documentService;
    }

    @Override
    public String display() {
        return "/panel/specimenPanel.xhtml";
    }

    @Override
    public String ressourceUri() {
        return "/specimen/" + idunit;
    }

    @Override
    public String displayHeader() {
        return "/panel/header/specimenPanelHeader.xhtml";
    }


    public LocalDate offsetDateTimeToLocalDate(OffsetDateTime offsetDT) {
        return offsetDT.toLocalDate();
    }

    public List<Concept> fetchChildrenOfConcept(Concept concept) {
        List<Concept> concepts;

        try {
            concepts = conceptService.findDirectSubConceptOf(concept);
        } catch (ErrorProcessingExpansionException e) {
            log.error(e.getMessage());
            log.debug(e.getMessage(), e);
            return new ArrayList<>();
        }

        return concepts;

    }


    public void refreshUnit() {

        // reinit
        hasUnsavedModifications = false;
        errorMessage = null;
        unit = null;

        try {

            unit = specimenService.findById(idunit);
            backupClone = new Specimen(unit);
            this.titleCodeOrTitle = unit.getFullIdentifier();

            initForms();

            // Get all the CHILDREN of the recording unit
            selectedCategoriesChildren = new ArrayList<>();
            totalChildrenCount = 0;
            // Get all the Parents of the recording unit
            selectedCategoriesParents = new ArrayList<>();
            totalParentsCount = 0;


        } catch (RuntimeException e) {
            this.errorMessage = "Failed to load specimen: " + e.getMessage();
        }


        historyVersion = historyService.findSpecimenHistory(unit);
        documents = documentService.findForSpecimen(unit);
    }

    @Override
    protected BaseLazyDataModel<Specimen> getLazyDataModelChildren() {
        return null;
    }

    @Override
    public BaseLazyDataModel<Specimen> getLazyDataModelParents() {
        return null;
    }

    @Override
    public void init() {
        try {

            // Details form
            detailsForm = new CustomForm.Builder()
                    .name("Details tab form")
                    .description("Contains the main form")
                    .addPanel(
                            new CustomFormPanel.Builder()
                                    .name("common.header.general")
                                    .isSystemPanel(true)
                                    .addRow(
                                            new CustomRow.Builder()
                                                    .addColumn(new CustomCol.Builder()
                                                            .readOnly(true)
                                                            .className(COLUMN_CLASS_NAME)
                                                            .field(specimenIdField)
                                                            .build())
                                                    .addColumn(new CustomCol.Builder()
                                                            .readOnly(false)
                                                            .className(COLUMN_CLASS_NAME)
                                                            .field(authorsField)
                                                            .build())
                                                    .addColumn(new CustomCol.Builder()
                                                            .readOnly(false)
                                                            .className(COLUMN_CLASS_NAME)
                                                            .field(collectorsField)
                                                            .build())
                                                    .addColumn(new CustomCol.Builder()
                                                            .readOnly(false)
                                                            .className(COLUMN_CLASS_NAME)
                                                            .field(specimenTypeField)
                                                            .build())
                                                    .addColumn(new CustomCol.Builder()
                                                            .readOnly(false)
                                                            .className(COLUMN_CLASS_NAME)
                                                            .field(collectionDateField)
                                                            .build())
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            // Details form
            overviewForm = new CustomForm.Builder()
                    .name("Overview tab form")
                    .description("Contains the overview")
                    .addPanel(
                            new CustomFormPanel.Builder()
                                    .name("common.header.general")
                                    .isSystemPanel(true)
                                    .addRow(
                                            new CustomRow.Builder()
                                                    .addColumn(new CustomCol.Builder()
                                                            .readOnly(false)
                                                            .className(COLUMN_CLASS_NAME)
                                                            .field(specimenTypeField)
                                                            .build())
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            activeTabIndex = 0;


            if (idunit == null) {
                this.errorMessage = "The ID of the specimen unit must be defined";
                return;
            }

            refreshUnit();

            if (this.unit == null) {
                log.error("The Specimen page should not be accessed without ID or by direct page path");
                errorMessage = "The Specimen page should not be accessed without ID or by direct page path";
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

        // Init system form answers
        formResponse = initializeFormResponse(detailsForm, unit);

    }

    @Override
    public void cancelChanges() {

        unit.setType(backupClone.getType());
        unit.setRecordingUnit(backupClone.getRecordingUnit());
        unit.setCategory(backupClone.getCategory());
        unit.setCreatedByInstitution(backupClone.getCreatedByInstitution());
        unit.setAuthor(backupClone.getAuthor());
        unit.setAuthors(backupClone.getAuthors());
        unit.setCollectors(backupClone.getCollectors());
        unit.setCollectionDate(backupClone.getCollectionDate());
        initForms();
    }

    @Override
    public void visualise(SpecimenHist history) {

    }

    @Override
    public void saveDocument() {

    }

    @Override
    public String getAutocompleteClass() {
        return "recording-unit-autocomplete";
    }

    @Override
    public void save(Boolean validated) {

        updateJpaEntityFromFormResponse(formResponse, unit);
        unit.setValidated(validated);
        if (Boolean.TRUE.equals(validated)) {
            unit.setValidatedBy(sessionSettingsBean.getAuthenticatedUser());
            unit.setValidatedAt(OffsetDateTime.now());
        } else {
            unit.setValidatedBy(null);
            unit.setValidatedAt(null);
        }

        try {
            specimenService.save(unit);
        } catch (FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.spatialUnits.updateFailed", unit.getFullIdentifier());
            return;
        }

        refreshUnit();
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getFullIdentifier());

    }

    public static class Builder {

        private final SpecimenPanel specimenPanel;

        public Builder(ObjectProvider<SpecimenPanel> specimenPanelProvider) {
            this.specimenPanel = specimenPanelProvider.getObject();
        }

        public SpecimenPanel.Builder id(Long id) {
            specimenPanel.setIdunit(id);
            return this;
        }

        public SpecimenPanel.Builder breadcrumb(PanelBreadcrumb breadcrumb) {
            specimenPanel.setBreadcrumb(breadcrumb);

            return this;
        }


        public SpecimenPanel build() {
            specimenPanel.init();
            return specimenPanel;
        }
    }


}
