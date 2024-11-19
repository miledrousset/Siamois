package fr.siamois.services;

import fr.siamois.infrastructure.repositories.ActionUnitRepository;
import fr.siamois.models.ActionUnit;
import fr.siamois.models.SpatialUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ActionUnitService {

    private final ActionUnitRepository actionUnitRepository;

    public ActionUnitService(ActionUnitRepository actionUnitRepository) {
        this.actionUnitRepository = actionUnitRepository;
    }

    public List<ActionUnit> findAllBySpatialUnitId(SpatialUnit spatialUnit)   {
        return actionUnitRepository.findAllBySpatialUnitId(spatialUnit.getId());
    }

}
