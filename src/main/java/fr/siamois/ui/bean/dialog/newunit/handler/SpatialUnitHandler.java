package fr.siamois.ui.bean.dialog.newunit.handler;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.exceptions.EntityAlreadyExistsException;
import fr.siamois.domain.models.form.customform.CustomForm;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.services.spatialunit.SpatialUnitService;
import fr.siamois.ui.bean.dialog.newunit.GenericNewUnitDialogBean;
import fr.siamois.ui.bean.dialog.newunit.UnitKind;
import org.springframework.stereotype.Component;

import java.util.List;

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

    @Override public void initFromContext(GenericNewUnitDialogBean<?> bean) { /* parents/enfants si besoin */ }

    @Override
    public List<SpatialUnit> getSpatialUnitOptions(SpatialUnit unit) {
        return List.of();
    }

    @Override
    public String getName(SpatialUnit unit) {
        return unit.getName();
    }


}
