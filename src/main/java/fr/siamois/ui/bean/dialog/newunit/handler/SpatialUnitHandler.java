package fr.siamois.ui.bean.dialog.newunit.handler;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.exceptions.EntityAlreadyExistsException;
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

    @Override public void initFromContext(GenericNewUnitDialogBean<?> bean) {
        SpatialUnit unit = (SpatialUnit) bean.getUnit();
        if(bean.getMultiHierarchyParent() != null) {
            unit.getParents().add((SpatialUnit) bean.getMultiHierarchyParent());
        }
        if(bean.getMultiHierarchyChild() != null) {
            unit.getChildren().add((SpatialUnit) bean.getMultiHierarchyChild());
        }
    }


    @Override
    public List<SpatialUnit> getSpatialUnitOptions(SpatialUnit unit) {
        return List.of();
    }

    @Override
    public String getName(SpatialUnit unit) {
        return unit.getName();
    }


}
