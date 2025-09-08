package fr.siamois.ui.bean.dialog.newunit.handler;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.exceptions.EntityAlreadyExistsException;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.spatialunit.SpatialUnitService;
import fr.siamois.ui.bean.dialog.newunit.GenericNewUnitDialogBean;
import fr.siamois.ui.bean.dialog.newunit.UnitKind;
import org.springframework.stereotype.Component;

@Component
public class SpatialUnitHandler implements INewUnitHandler<SpatialUnit> {

    private final SpatialUnitService spatialUnitService;

    public SpatialUnitHandler(SpatialUnitService spatialUnitService) {
        this.spatialUnitService = spatialUnitService;
    }

    @Override public UnitKind kind() { return UnitKind.SPATIAL; }
    @Override public SpatialUnit newEmpty() { return new SpatialUnit(); }
    @Override public SpatialUnit save(UserInfo u, SpatialUnit unit) throws EntityAlreadyExistsException { return spatialUnitService.save(u, unit); }
    @Override public String dialogWidgetVar() { return "newUnitDiag"; }
    @Override public String successMessageCode() { return "common.entity.spatialUnits.updated"; }
    @Override public String viewUrlFor(Long id) { return "/spatial-unit/" + id; }
    @Override public CustomForm formLayout() { return SpatialUnit.NEW_UNIT_FORM; }
    @Override public void onInitFromContext(GenericNewUnitDialogBean<?> bean) { /* parents/enfants si besoin */ }

    @Override
    public String getName(SpatialUnit unit) {
        return unit.getName();
    }

    @Override
    public String getRessourceUri() {
        return "/spatial-unit/new";
    }

    @Override
    public String getTitle() {
        return "Nouvelle unité spatiale";
    }

    @Override
    public String styleClassName() {
        return "spatial-unit-panel";
    }

    @Override
    public String getIcon() {
        return "bi bi-pencil-square";
    }

    @Override
    public String getAutocompleteClass() {
        return "spatial-unit-autocomplete";
    }

}
