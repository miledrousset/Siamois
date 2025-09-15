package fr.siamois.ui.bean.dialog.actionunit;


import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.services.spatialunit.SpatialUnitService;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.specimen.SpecimenService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.RedirectBean;
import fr.siamois.ui.bean.dialog.AbstractNewUnitDialogBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.panel.models.panel.single.AbstractSingleEntity;
import fr.siamois.ui.lazydatamodel.BaseActionUnitLazyDataModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.faces.bean.SessionScoped;

import java.util.Set;


@Slf4j
@Component
@Getter
@Setter
@SessionScoped
@EqualsAndHashCode(callSuper = true)
public class NewActionUnitDialogBean extends AbstractNewUnitDialogBean<ActionUnit> {

    private final transient ActionUnitService actionUnitService;
    private final transient SpatialUnitService spatialUnitService;
    private final transient SpecimenService specimenService;

    public NewActionUnitDialogBean(LangBean langBean, FlowBean flowBean,
                                   SpatialUnitService spatialUnitService,
                                   ActionUnitService actionUnitService,
                                   SpecimenService specimenService,
                                   AbstractSingleEntity.Deps deps,
                                   RedirectBean redirectBean) {
        super(langBean, redirectBean, flowBean, deps);
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
        this.specimenService = specimenService;
    }

    @Override
    protected void createEmptyUnit() {
        unit = new ActionUnit();
    }

    @Override
    protected void persistUnit() {
        unit = actionUnitService.save(sessionSettingsBean.getUserInfo(), unit, unit.getType());
    }

    @Override
    protected String getDialogWidgetVar() {
        return "newActionUnitDiag";
    }

    @Override
    protected String getSuccessMessageCode() {
        return "common.entity.spatialUnits.updated";
    }

    @Override
    protected void openPanel(Long unitId) {
        flowBean.addActionUnitPanel(unitId);
    }

    @Override
    public String display() {
        return "";
    }

    @Override
    public String ressourceUri() {
        return "/action-unit/new";
    }

    @Override
    public void initForms() {

        // Details form
        detailsForm = ActionUnit.NEW_UNIT_FORM;

        // Init system form answers
        formResponse = initializeFormResponse(detailsForm, unit);

    }

    @Override
    public String getAutocompleteClass() {
        return "action-unit-autocomplete";
    }

    @Override
    protected String unitName() {
        return unit.getName();
    }

    @Override
    protected Long getUnitId() {
        return unit.getId();
    }

    // Init when creating with button on top of table
    public void init(BaseActionUnitLazyDataModel lazyDataModel) {
        reset();
        createEmptyUnit();
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
        createEmptyUnit();
        unit.getSpatialContext().add(spatialUnitService.findById(spatialUnitId));
        unit.setAuthor(sessionSettingsBean.getAuthenticatedUser());
        unit.setCreatedByInstitution(sessionSettingsBean.getSelectedInstitution());
        this.setToUpdate = setToUpdate;
        initForms();
    }
}
