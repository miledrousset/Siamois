package fr.siamois.ui.bean.dialog.recordingunit;

import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.form.customfield.*;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;



@Slf4j
@EqualsAndHashCode(callSuper = true)
@Component
@Getter
@Setter
@SessionScoped
public class NewRecordingUnitBean extends AbstractSingleEntity<RecordingUnit> implements Serializable {

    // Deps
    private final transient RecordingUnitService recordingUnitService;
    private final transient LangBean langBean;
    private final transient FlowBean flowBean;
    private final transient ActionUnitService actionUnitService;

    // Locals
    private ActionUnit actionUnit; // parent action unit for the new recording unit
    private SpatialUnit spatialUnit; // parent spatial unit for the new recording unit
    private BaseRecordingUnitLazyDataModel lazyDataModel; // lazy data model to update after saving

    private static final String COLUMN_CLASS_NAME = "ui-g-12 ui-md-6 ui-lg-6";
    private static final String UPDATE_FAILED = "common.entity.spatialUnits.updateFailed";
    
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

    public NewRecordingUnitBean(RecordingUnitService recordingUnitService,
                                LangBean langBean, FlowBean flowBean,
                                ActionUnitService actionUnitService,
                                SessionSettingsBean sessionSettingsBean,
                                FieldConfigurationService fieldConfigurationService) {
        super(sessionSettingsBean, fieldConfigurationService);
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

    @Override
    public List<SpatialUnit> getSpatialUnitOptions() {

        // Return the spatial context of the parent action
        if (actionUnit != null) {
            return new ArrayList<>(actionUnit.getSpatialContext());
        }

        return List.of();
    }


    private void reset() {
        unit = null;
        actionUnit = null;
        formResponse = null;
        lazyDataModel = null;
    }

    public void init(BaseRecordingUnitLazyDataModel lazyDataModel) {
        reset();
        unit = new RecordingUnit();
        // Attempt safe cast to access getActionUnit()
        if (lazyDataModel instanceof RecordingUnitInActionUnitLazyDataModel typedModel) {
            actionUnit = actionUnitService.findById(typedModel.getActionUnit().getId());
            unit.setCreatedByInstitution(actionUnit.getCreatedByInstitution());
            unit.setActionUnit(actionUnit);
            unit.setCreatedByInstitution(actionUnit.getCreatedByInstitution());
        } else {
            unit.setCreatedByInstitution(sessionSettingsBean.getSelectedInstitution());
        }
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        this.lazyDataModel = lazyDataModel;
        unit.setActionUnit(actionUnit);
        unit.setExcavators(List.of(sessionSettingsBean.getAuthenticatedUser()));
        unit.setAuthors(List.of(sessionSettingsBean.getAuthenticatedUser()));
        unit.setStartDate(OffsetDateTime.now());
        initForms();
    }

    public void createRu() {


        try {
            updateJpaEntityFromFormResponse(formResponse, unit);
            unit.setValidated(false);
            unit = recordingUnitService.save(unit, unit.getType(), List.of(), List.of(), List.of());


            if (lazyDataModel != null) {
                lazyDataModel.addRowToModel(unit);
            }
        } catch (FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED, unit.getFullIdentifier());
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
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED, unit.getFullIdentifier());
            throw e;
        }


        // Open new panel
        PrimeFaces.current().executeScript("PF('newRecordingUnitDiag').hide();handleScrollToTop();");
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getFullIdentifier());
        flowBean.addRecordingUnitPanel(unit.getId());

    }

    public void create() {

        try {
            createRu();
        } catch (FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED, unit.getFullIdentifier());
            throw e;
        }

        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getFullIdentifier());
        PrimeFaces.current().executeScript("PF('newRecordingUnitDiag').hide()");


    }
}
