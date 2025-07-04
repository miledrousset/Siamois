package fr.siamois.ui.bean.dialog.spatialunit;


import fr.siamois.domain.models.exceptions.spatialunit.SpatialUnitAlreadyExistsException;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectOneFromFieldCode;
import fr.siamois.domain.models.form.customfield.CustomFieldText;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.specimen.SpecimenService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.panel.single.AbstractSingleEntity;
import fr.siamois.ui.lazydatamodel.BaseSpatialUnitLazyDataModel;
import fr.siamois.ui.lazydatamodel.SpatialUnitChildrenLazyDataModel;
import fr.siamois.ui.lazydatamodel.SpatialUnitParentsLazyDataModel;
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
import java.util.HashSet;
import java.util.LinkedHashSet;
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


    public NewSpatialUnitDialogBean(
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
                unit.setParents(new HashSet<>());
                unit.getParents().add(spatialUnitParent);
            } else if (lazyDataModel instanceof SpatialUnitParentsLazyDataModel typedModel) {
                SpatialUnit spatialUnitChild = spatialUnitService.findById(typedModel.getSpatialUnit().getId());
                unit.setChildren(new HashSet<>());
                unit.getChildren().add(spatialUnitChild);
            }
        }
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        unit.setCreatedByInstitution(sessionSettingsBean.getSelectedInstitution());
        initForms();
    }

    // Init when creating with button in table column
    public void init(String isSetChildrenOrParents,
                     Long childOrParentId,
                     Set<SpatialUnit> setToUpdate) {


        reset();
        unit = new SpatialUnit();
        // Set parents or children based on children or parents
        if (Objects.equals(isSetChildrenOrParents, "children")
                && setToUpdate != null
                && childOrParentId != null) {
            SpatialUnit spatialUnitParent = spatialUnitService.findById(childOrParentId);
            unit.setParents(new HashSet<>());
            unit.getParents().add(spatialUnitParent);
            this.setToUpdate = setToUpdate;
        }else if (Objects.equals(isSetChildrenOrParents, "parents")
                && setToUpdate != null
                && childOrParentId != null) {
            SpatialUnit spatialUnitChild = spatialUnitService.findById(childOrParentId);
            unit.setChildren(new HashSet<>());
            unit.getChildren().add(spatialUnitChild);
            this.setToUpdate = setToUpdate;
        }
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        unit.setCreatedByInstitution(sessionSettingsBean.getSelectedInstitution());
        initForms();
    }




    public void createSU() throws SpatialUnitAlreadyExistsException {

            updateJpaEntityFromFormResponse(formResponse, unit);
            unit.setValidated(false);
            unit = spatialUnitService.save(sessionSettingsBean.getUserInfo(),
                    unit);

            // if the request came from a set or a lazy model, update the set or lazy model
            if (lazyDataModel != null) {
                lazyDataModel.addRowToModel(unit);
            }
            if (setToUpdate != null) {
                LinkedHashSet<SpatialUnit> newSet = new LinkedHashSet<>();
                newSet.add(unit);
                newSet.addAll(setToUpdate);
                setToUpdate.clear();
                setToUpdate.addAll(newSet);
            }

    }

    @Override
    public String getAutocompleteClass() {
        return "spatial-unit-autocomplete";
    }

    public void createAndOpen() {

        try {
            createSU();
        } catch (RuntimeException | SpatialUnitAlreadyExistsException e) {
            log.error(e.getMessage());
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED_MESSAGE_CODE, unit.getName());
            return;
        }


        // Open new panel
        PrimeFaces.current().executeScript("PF('newSpatialUnitDiag').hide();handleScrollToTop();");
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getName());
        flowBean.addSpatialUnitPanel(unit.getId());
        flowBean.updateHomePanel();
    }

    public void create() {

        try {
            createSU();
        } catch (RuntimeException | SpatialUnitAlreadyExistsException e) {
            log.error(e.getMessage());
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED_MESSAGE_CODE, unit.getName());
            return;
        }
        PrimeFaces.current().executeScript("PF('newSpatialUnitDiag').hide();");
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getName());
        flowBean.updateHomePanel();
    }
}
