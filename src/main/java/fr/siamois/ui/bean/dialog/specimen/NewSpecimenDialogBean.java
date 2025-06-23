package fr.siamois.ui.bean.dialog.specimen;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.form.customfield.*;

import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;


import fr.siamois.domain.models.recordingunit.RecordingUnit;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.specimen.SpecimenService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.panel.single.AbstractSingleEntity;
import fr.siamois.ui.lazydatamodel.BaseRecordingUnitLazyDataModel;
import fr.siamois.ui.lazydatamodel.RecordingUnitInActionUnitLazyDataModel;
import fr.siamois.utils.MessageUtils;
import jakarta.faces.component.UIComponent;
import jakarta.faces.event.AjaxBehaviorEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import javax.faces.bean.SessionScoped;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import java.util.List;


@Slf4j
@Component
@Getter
@Setter
@SessionScoped
public class NewSpecimenDialogBean extends AbstractSingleEntity<Specimen> implements Serializable {

    // Deps
    private final transient RecordingUnitService recordingUnitService;
    private final transient LangBean langBean;
    private final transient FlowBean flowBean;
    private final transient ActionUnitService actionUnitService;
    private final transient SpecimenService specimenService;

    // Locals
    private RecordingUnit recordingUnit; // parent ru

    private static final String COLUMN_CLASS_NAME = "ui-g-12";

    // ----------- Concepts for system fields
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

    private CustomFieldSelectOneFromFieldCode specimenCategoryField = new CustomFieldSelectOneFromFieldCode.Builder()
            .label("specimen.field.category")
            .isSystemField(true)
            .valueBinding("category")
            .styleClass("mr-2 specimen-type-chip")
            .iconClass("bi bi-box2")
            .fieldCode(Specimen.CAT_FIELD)
            .concept(specimenCategoryConcept)
            .build();


    private CustomFieldDateTime collectionDateField = new CustomFieldDateTime.Builder()
            .label("specimen.field.collectionDate")
            .isSystemField(true)
            .valueBinding("collectionDate")
            .showTime(false)
            .concept(collectionDateConcept)
            .build();



    public NewSpecimenDialogBean(RecordingUnitService recordingUnitService,
                                 LangBean langBean,
                                 FlowBean flowBean,
                                 SessionSettingsBean sessionSettingsBean,
                                 FieldConfigurationService fieldConfigurationService,
                                 ActionUnitService actionUnitService, SpecimenService specimenService) {
        super(sessionSettingsBean, fieldConfigurationService);
        this.recordingUnitService = recordingUnitService;
        this.langBean = langBean;
        this.flowBean = flowBean;
        this.actionUnitService = actionUnitService;
        this.specimenService = specimenService;
    }

    @Override
    public void setFieldConceptAnswerHasBeenModified(AjaxBehaviorEvent event) {
        UIComponent component = event.getComponent();
        CustomField field = (CustomField) component.getAttributes().get("field");

        formResponse.getAnswers().get(field).setHasBeenModified(true);
    }

    @Override
    public void initForms() {

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
                                                        .field(specimenCategoryField)
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
                                ).build()
                )
                .build();

        // Init system form answers
        formResponse = initializeFormResponse(detailsForm, unit);

    }

    @Override
    public String display() {
        return "";
    }

    @Override
    public String ressourceUri() {
        return "/recording-unit/new";
    }



    private void reset() {
        unit = null;
        recordingUnit = null;
        formResponse = null;
    }

    public void init(RecordingUnit ru) {
        reset();
        unit = new Specimen();
        recordingUnit = ru;
        // Attempt safe cast to access getActionUnit()
        unit.setCreatedByInstitution(ru.getCreatedByInstitution());
        unit.setRecordingUnit(ru);
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        unit.setAuthors(List.of(sessionSettingsBean.getAuthenticatedUser()));
        unit.setCollectors(List.of(sessionSettingsBean.getAuthenticatedUser()));
        unit.setCollectionDate(OffsetDateTime.now());
        initForms();
    }

    public void createSpecimen() {


        try {
            updateJpaEntityFromFormResponse(formResponse, unit);
            unit.setValidated(false);
            unit = (Specimen) specimenService.save(unit);

        } catch (FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.spatialUnits.updateFailed", unit.getFullIdentifier());
        }


    }

    @Override
    public String getAutocompleteClass() {
        return "recording-unit-autocomplete";
    }

    public void createAndOpen() {

        try {
            createSpecimen();
        } catch (RuntimeException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.spatialUnits.updateFailed", unit.getFullIdentifier());
            throw e;
        }


        // Open new panel
        flowBean.addSpecimenPanel(unit.getId());

    }

    public void create() {

        try {
            createSpecimen();
        } catch (FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.spatialUnits.updateFailed", unit.getFullIdentifier());
            throw e;
        }

        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getFullIdentifier());


    }
}
