package fr.siamois.services;

import fr.siamois.models.ActionUnit;
import fr.siamois.models.RecordingUnit;
import fr.siamois.models.SpatialUnit;

import fr.siamois.repositories.ActionUnitRepository;
import fr.siamois.repositories.RecordingUnitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ActionUnitService {

    @Autowired
    ActionUnitRepository actionUnitRepository;

    public List<ActionUnit> findAllBySpatialUnitId(SpatialUnit spatialUnit)   {
        return actionUnitRepository.findAllBySpatialUnitId(spatialUnit.getId());
    }

}
