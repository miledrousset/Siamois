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
import fr.siamois.ui.bean.LangBean;
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

    // Locals
    private RecordingUnit recordingUnit; // parent ru

    private static final String COLUMN_CLASS_NAME = "ui-g-12 ui-md-6 ui-lg-6";

    // ----------- Concepts for system fields
    // Authors
    private Concept authorsConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4286194")
            .build();
    // Excavators
    private Concept excavatorsConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4286195")
            .build();

    // Recording Unit type
    private Concept recordingUnitTypeConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4282367")
            .build();

    // Date
    private Concept openingDateConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4286198")
            .build();


    // Spatial Unit
    private Concept spatialUnitConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4286245")
            .build();


    private CustomFieldSelectMultiplePerson authorsField = new CustomFieldSelectMultiplePerson.Builder()
            .label("recordingunit.field.authors")
            .isSystemField(true)
            .valueBinding("authors")
            .concept(authorsConcept)
            .build();

    private CustomFieldSelectMultiplePerson excavatorsField = new CustomFieldSelectMultiplePerson.Builder()
            .label("recordingunit.field.excavators")
            .isSystemField(true)
            .valueBinding("excavators")
            .concept(excavatorsConcept)
            .build();

    private CustomFieldSelectOneFromFieldCode recordingUnitTypeField = new CustomFieldSelectOneFromFieldCode.Builder()
            .label("spatialunit.field.type")
            .isSystemField(true)
            .valueBinding("type")
            .styleClass("mr-2 recording-unit-type-chip")
            .iconClass("bi bi-pencil-square")
            .fieldCode(RecordingUnit.TYPE_FIELD_CODE)
            .concept(recordingUnitTypeConcept)
            .build();


    private CustomFieldDateTime openingDateField = new CustomFieldDateTime.Builder()
            .label("recordingunit.field.openingDate")
            .isSystemField(true)
            .valueBinding("startDate")
            .showTime(false)
            .concept(openingDateConcept)
            .build();

    private CustomFieldSelectOneSpatialUnit spatialUnitField = new CustomFieldSelectOneSpatialUnit.Builder()
            .label("recordingunit.field.spatialUnit")
            .isSystemField(true)
            .valueBinding("spatialUnit")
            .concept(spatialUnitConcept)
            .build();

    public NewSpecimenDialogBean(RecordingUnitService recordingUnitService, LangBean langBean, FlowBean flowBean, ActionUnitService actionUnitService) {
        this.recordingUnitService = recordingUnitService;
        this.langBean = langBean;
        this.flowBean = flowBean;
        this.actionUnitService = actionUnitService;
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
                                                        .field(spatialUnitField)
                                                        .build())
                                                .addColumn(new CustomCol.Builder()
                                                        .readOnly(false)
                                                        .className(COLUMN_CLASS_NAME)
                                                        .field(authorsField)
                                                        .build())
                                                .addColumn(new CustomCol.Builder()
                                                        .readOnly(false)
                                                        .className(COLUMN_CLASS_NAME)
                                                        .field(excavatorsField)
                                                        .build())
                                                .addColumn(new CustomCol.Builder()
                                                        .readOnly(false)
                                                        .className(COLUMN_CLASS_NAME)
                                                        .field(recordingUnitTypeField)
                                                        .build())
                                                .addColumn(new CustomCol.Builder()
                                                        .readOnly(false)
                                                        .className(COLUMN_CLASS_NAME)
                                                        .field(openingDateField)
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
        unit.setCollectionDate(OffsetDateTime.now());
        initForms();
    }

    public void createRu() {


        try {
            updateJpaEntityFromFormResponse(formResponse, unit);
            unit.setValidated(false);
            // todo;

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
            createRu();
        } catch (RuntimeException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.spatialUnits.updateFailed", unit.getFullIdentifier());
            throw e;
        }


        // Open new panel
        //todo: flowBean.addRecordingUnitPanel(unit.getId());

    }

    public void create() {

        try {
            createRu();
        } catch (FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, "common.entity.spatialUnits.updateFailed", unit.getFullIdentifier());
            throw e;
        }

        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getFullIdentifier());


    }
}
