package fr.siamois.ui.bean.dialog.actionunit;


import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectMultipleSpatialUnitTree;
import fr.siamois.domain.models.form.customfield.CustomFieldSelectOneFromFieldCode;
import fr.siamois.domain.models.form.customfield.CustomFieldText;
import fr.siamois.domain.models.form.customform.CustomCol;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.form.customform.CustomFormPanel;
import fr.siamois.domain.models.form.customform.CustomRow;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.spatialunit.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.spatialunit.SpatialUnitTreeService;
import fr.siamois.domain.services.specimen.SpecimenService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.panel.single.AbstractSingleEntity;
import fr.siamois.ui.lazydatamodel.BaseActionUnitLazyDataModel;
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
public class NewActionUnitDialogBean extends AbstractSingleEntity<ActionUnit> implements Serializable {

    // Deps
    private final transient SpatialUnitService spatialUnitService;
    private final transient LangBean langBean;
    private final transient FlowBean flowBean;
    private final transient ActionUnitService actionUnitService;
    private final transient SpecimenService specimenService;

    // Locals
    private BaseActionUnitLazyDataModel lazyDataModel; // lazymodel to be updated after creation
    private Set<ActionUnit> setToUpdate; // set to be updated after creation


    private static final String COLUMN_CLASS_NAME = "ui-g-12";
    private static final String UPDATE_FAILED_MESSAGE_CODE = "common.entity.spatialUnits.updateFailed";

    public static String generateRandomActionUnitIdentifier() {
        return "2025";
    }



    public NewActionUnitDialogBean(
            LangBean langBean,
            FlowBean flowBean,
            SessionSettingsBean sessionSettingsBean,
            FieldConfigurationService fieldConfigurationService, SpatialUnitService spatialUnitService,
            ActionUnitService actionUnitService, SpecimenService specimenService,
            SpatialUnitTreeService spatialUnitTreeService) {
        super(sessionSettingsBean, fieldConfigurationService, spatialUnitTreeService);
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
        detailsForm = ActionUnit.NEW_UNIT_FORM;

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
        lazyDataModel = null;
        setToUpdate = null;

    }

    public void init() {
        reset();
        unit = new ActionUnit();
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        unit.setCreatedByInstitution(sessionSettingsBean.getSelectedInstitution());
        initForms();
    }

    // Init when creating with button on top of table
    public void init(BaseActionUnitLazyDataModel lazyDataModel) {
        reset();
        unit = new ActionUnit();
        // Set parents or children based on lazy model type
        if (lazyDataModel != null) {
            this.lazyDataModel = lazyDataModel;
            // todo add handling children/parents list
        }
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        unit.setCreatedByInstitution(sessionSettingsBean.getSelectedInstitution());
        initForms();
    }

    // Init when creating with button in table, actions column
    public void init(
                     Long spatialUnitId,
                     Set<ActionUnit> setToUpdate) {


        reset();
        unit = new ActionUnit();
        unit.getSpatialContext().add(spatialUnitService.findById(spatialUnitId));
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        unit.setCreatedByInstitution(sessionSettingsBean.getSelectedInstitution());
        this.setToUpdate = setToUpdate;
        initForms();
    }


    public void createSU() {

        updateJpaEntityFromFormResponse(formResponse, unit);
        unit.setValidated(false);
        unit = actionUnitService.save(sessionSettingsBean.getUserInfo(),
                unit, unit.getType());

        // if the request came from a set or a lazy model, update the set or lazy model
        if (lazyDataModel != null) {
            lazyDataModel.addRowToModel(unit);
        }
        if (setToUpdate != null) {
            LinkedHashSet<ActionUnit> newSet = new LinkedHashSet<>();
            newSet.add(unit);
            newSet.addAll(setToUpdate);
            setToUpdate.clear();
            setToUpdate.addAll(newSet);
        }

    }

    @Override
    public String getAutocompleteClass() {
        return "action-unit-autocomplete";
    }

    public void createAndOpen() {

        try {
            createSU();
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED_MESSAGE_CODE, unit.getName());
            return;
        }


        // Open new panel
        PrimeFaces.current().executeScript("PF('newActionUnitDiag').hide();handleScrollToTop();");
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getName());
        flowBean.addSpatialUnitPanel(unit.getId());
        flowBean.updateHomePanel();
    }

    public void create() {

        try {
            createSU();
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            MessageUtils.displayErrorMessage(langBean, UPDATE_FAILED_MESSAGE_CODE, unit.getName());
            return;
        }
        PrimeFaces.current().executeScript("PF('newActionUnitDiag').hide();");
        MessageUtils.displayInfoMessage(langBean, "common.entity.spatialUnits.updated", unit.getName());
        flowBean.updateHomePanel();
    }
}
