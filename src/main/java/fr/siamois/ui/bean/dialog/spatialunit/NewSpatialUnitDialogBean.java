package fr.siamois.ui.bean.dialog.spatialunit;


import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.exceptions.spatialunit.SpatialUnitAlreadyExistsException;
import fr.siamois.domain.models.form.customfield.*;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.recordingunit.RecordingUnitService;
import fr.siamois.domain.services.specimen.SpecimenService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.panel.single.AbstractSingleEntity;
import fr.siamois.ui.lazydatamodel.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Slf4j
@Component
@Getter
@Setter
@SessionScoped
@EqualsAndHashCode(callSuper = true)
public class NewSpatialUnitDialogBean extends AbstractSingleEntity<SpatialUnit> implements Serializable {

    // Deps
    private final transient SpatialUnitService spatialUnitService;
    private final transient LangBean langBean;
    private final transient FlowBean flowBean;
    private final transient ActionUnitService actionUnitService;
    private final transient SpecimenService specimenService;

    // Locals
    private BaseSpatialUnitLazyDataModel lazyDataModel; // lazymodel to be updated after creation
    private Set<SpatialUnit> setToUpdate; // set to be updated after creation
    private String isSetChildrenOrParents;
    private Long childOrParentId;


    private static final String COLUMN_CLASS_NAME = "ui-g-12";
    private static final String UPDATE_FAILED_MESSAGE_CODE = "common.entity.spatialUnits.updateFailed";

    // ----------- Concepts for system fields


    // uni category
    private Concept spatialUnitTypeConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4282365")
            .build();
    // unit name
    private Concept nameConcept = new Concept.Builder()
            .vocabulary(SYSTEM_THESO)
            .externalId("4285848")
            .build();


    // --------------- Fields


    private CustomFieldSelectOneFromFieldCode spatialUnitTypeField = new CustomFieldSelectOneFromFieldCode.Builder()
            .label("specimen.field.category")
            .isSystemField(true)
            .valueBinding("category")
            .styleClass("mr-2 specimen-type-chip")
            .iconClass("bi bi-box2")
            .fieldCode(SpatialUnit.CATEGORY_FIELD_CODE)
            .concept(spatialUnitTypeConcept)
            .build();

    private CustomFieldText nameField = new CustomFieldText.Builder()
            .label("common.label.name")
            .isSystemField(true)
            .valueBinding("name")
            .concept(nameConcept)
            .build();


    public NewSpatialUnitDialogBean(RecordingUnitService recordingUnitService,
                                    LangBean langBean,
                                    FlowBean flowBean,
                                    SessionSettingsBean sessionSettingsBean,
                                    FieldConfigurationService fieldConfigurationService, SpatialUnitService spatialUnitService,
                                    ActionUnitService actionUnitService, SpecimenService specimenService) {
        super(sessionSettingsBean, fieldConfigurationService);
        this.spatialUnitService = spatialUnitService;
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
                                                        .field(nameField)
                                                        .build())
                                                .addColumn(new CustomCol.Builder()
                                                        .readOnly(false)
                                                        .className(COLUMN_CLASS_NAME)
                                                        .field(spatialUnitTypeField)
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
        return "/spatial-unit/new";
    }


    private void reset() {
        unit = null;
        formResponse = null;
        lazyDataModel= null;
        setToUpdate= null;
        isSetChildrenOrParents= null;
        childOrParentId= null;

    }

    public void init() {
        reset();
        unit = new SpatialUnit();
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        unit.setCreatedByInstitution(sessionSettingsBean.getSelectedInstitution());
        initForms();
    }

    // Init when creating with button on top of table
    public void init(BaseSpatialUnitLazyDataModel lazyDataModel) {
        reset();
        unit = new SpatialUnit();
        // Set parents or children based on lazy model type
        if(lazyDataModel != null) {
            this.lazyDataModel = lazyDataModel;
            if (lazyDataModel instanceof SpatialUnitChildrenLazyDataModel typedModel) {
                SpatialUnit spatialUnitParent = spatialUnitService.findById(typedModel.getSpatialUnit().getId());
                unit.setChildren(new HashSet<>());
                unit.getChildren().add(spatialUnitParent);
            } else if (lazyDataModel instanceof SpatialUnitParentsLazyDataModel typedModel) {
                SpatialUnit spatialUnitChild = spatialUnitService.findById(typedModel.getSpatialUnit().getId());
                unit.setParents(new HashSet<>());
                unit.getParents().add(spatialUnitChild);
            }
        }
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        unit.setCreatedByInstitution(sessionSettingsBean.getSelectedInstitution());
        initForms();
    }




    public void createSU() {


        try {
            updateJpaEntityFromFormResponse(formResponse, unit);
            unit.setValidated(false);
            unit = spatialUnitService.save(sessionSettingsBean.getUserInfo(),
                    unit);

            if (lazyDataModel != null) {
                lazyDataModel.addRowToModel(unit);
            }

        } catch (Exception e) {
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED_MESSAGE_CODE, unit.getName());
        }


    }

    @Override
    public String getAutocompleteClass() {
        return "spatial-unit-autocomplete";
    }

    public void createAndOpen() {

        try {
            createSU();
        } catch (RuntimeException e) {
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED_MESSAGE_CODE, unit.getName());
            throw e;
        }


        // Open new panel
        PrimeFaces.current().executeScript("PF('newSpatialUnitDiag').hide();handleScrollToTop();");
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getName());
        flowBean.addSpatialUnitPanel(unit.getId());

    }

    public void create() {

        try {
            createSU();
        } catch (FailedRecordingUnitSaveException e) {
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED_MESSAGE_CODE, unit.getName());
            throw e;
        }
        PrimeFaces.current().executeScript("PF('newSpatialUnitDiag').hide();");
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getName());


    }
}
