package fr.siamois.ui.bean.dialog.newunit;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import org.springframework.stereotype.Component;

@Component
public class SpatialUnitHandler implements INewUnitHandler<SpatialUnit> {

    @Override public UnitKind kind() { return UnitKind.SPATIAL; }
    @Override public SpatialUnit newEmpty() { return new SpatialUnit(); }
    @Override public SpatialUnit save(UserInfo u, SpatialUnit unit) throws Exception { return spatialUnitService.save(u, unit); }
    @Override public String dialogWidgetVar() { return "newSpatialUnitDiag"; }
    @Override public String successMessageCode() { return "common.entity.spatialUnits.updated"; }
    @Override public String viewUrlFor(Long id) { return "/spatial-unit/" + id; }
    @Override public CustomForm formLayout() { return SpatialUnit.NEW_UNIT_FORM; }
    @Override public void onInitFromContext(GenericNewUnitDialogBean<?> bean) { /* parents/enfants si besoin */ }

}
