package fr.siamois.ui.bean.dialog.spatialunit;

import fr.siamois.domain.models.exceptions.spatialunit.SpatialUnitAlreadyExistsException;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.actionunit.ActionUnitService;
import fr.siamois.domain.services.spatialunit.SpatialUnitService;
import fr.siamois.domain.services.spatialunit.SpatialUnitTreeService;
import fr.siamois.domain.services.specimen.SpecimenService;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.ui.bean.LangBean;
import fr.siamois.ui.bean.SessionSettingsBean;
import fr.siamois.ui.bean.panel.FlowBean;
import fr.siamois.ui.bean.dialog.AbstractNewUnitDialogBean;
import fr.siamois.ui.lazydatamodel.BaseSpatialUnitLazyDataModel;
import fr.siamois.ui.lazydatamodel.SpatialUnitChildrenLazyDataModel;
import fr.siamois.ui.lazydatamodel.SpatialUnitParentsLazyDataModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
@Getter
@Setter
@SessionScoped
@EqualsAndHashCode(callSuper = true)
public class NewSpatialUnitDialogBean extends AbstractNewUnitDialogBean<SpatialUnit> implements Serializable {

    private final transient SpatialUnitService spatialUnitService;
    private final transient ActionUnitService actionUnitService;
    private final transient SpecimenService specimenService;

    public NewSpatialUnitDialogBean(LangBean langBean,
                                    FlowBean flowBean,
                                    SessionSettingsBean sessionSettingsBean,
                                    FieldConfigurationService fieldConfigurationService,
                                    SpatialUnitService spatialUnitService,
                                    ActionUnitService actionUnitService,
                                    SpecimenService specimenService,
                                    SpatialUnitTreeService spatialUnitTreeService) {
        super(sessionSettingsBean, fieldConfigurationService, spatialUnitTreeService, langBean, flowBean);
        this.spatialUnitService = spatialUnitService;
        this.actionUnitService = actionUnitService;
        this.specimenService = specimenService;
    }

    @Override
    protected void createEmptyUnit() {
        unit = new SpatialUnit();
    }

    @Override
    protected void persistUnit() throws SpatialUnitAlreadyExistsException {
        unit = spatialUnitService.save(sessionSettingsBean.getUserInfo(), unit);
    }

    @Override
    protected String getDialogWidgetVar() {
        return "newSpatialUnitDiag";
    }

    @Override
    public void initForms() {

        // Details form
        detailsForm = SpatialUnit.NEW_UNIT_FORM;

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

    @Override
    protected String getSuccessMessageCode() {
        return "common.entity.spatialUnits.updated";
    }

    @Override
    protected void openPanel(Long unitId) {
        flowBean.addSpatialUnitPanel(unitId);
    }

    @Override
    public String getAutocompleteClass() {
        return "spatial-unit-autocomplete";
    }

    @Override
    protected String unitName() {
        return unit.getName();
    }

    @Override
    protected Long getUnitId() {
        return unit.getId();
    }

    // Init depuis le bouton au-dessus du tableau
    public void init(BaseSpatialUnitLazyDataModel lazyDataModel) {
        init();
        if (lazyDataModel != null) {
            this.lazyDataModel = lazyDataModel;
            if (lazyDataModel instanceof SpatialUnitChildrenLazyDataModel typedModel) {
                SpatialUnit parent = spatialUnitService.findById(typedModel.getSpatialUnit().getId());
                unit.setParents(new HashSet<>());
                unit.getParents().add(parent);
            } else if (lazyDataModel instanceof SpatialUnitParentsLazyDataModel typedModel) {
                SpatialUnit child = spatialUnitService.findById(typedModel.getSpatialUnit().getId());
                unit.setChildren(new HashSet<>());
                unit.getChildren().add(child);
            }
        }
    }

    // Init depuis une ligne de tableau (enfants ou parents)
    public void init(String isSetChildrenOrParents, Long relatedId, Set<SpatialUnit> setToUpdate) {
        init();
        if (Objects.equals(isSetChildrenOrParents, "children") && relatedId != null) {
            SpatialUnit parent = spatialUnitService.findById(relatedId);
            unit.setParents(new HashSet<>());
            unit.getParents().add(parent);
        } else if (Objects.equals(isSetChildrenOrParents, "parents") && relatedId != null) {
            SpatialUnit child = spatialUnitService.findById(relatedId);
            unit.setChildren(new HashSet<>());
            unit.getChildren().add(child);
        }
        this.setToUpdate = setToUpdate;
    }

    
}
