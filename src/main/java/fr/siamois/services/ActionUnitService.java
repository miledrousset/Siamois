package fr.siamois.services;

import fr.siamois.models.ActionUnit;
import fr.siamois.models.RecordingUnit;
import fr.siamois.models.SpatialUnit;

import fr.siamois.models.exceptions.ActionUnitNotFoundException;

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

    private final ActionUnitRepository actionUnitRepository;

    public ActionUnitService(ActionUnitRepository actionUnitRepository) {
        this.actionUnitRepository = actionUnitRepository;
    }

    public List<ActionUnit> findAllBySpatialUnitId(SpatialUnit spatialUnit)   {
        return actionUnitRepository.findAllBySpatialUnitId(spatialUnit.getId());
    }

    /**
     * Find a action unit by its ID
     *
     * @param id The ID of the action unit
     * @return The ActionUnit having the given ID
     * @throws ActionUnitNotFoundException If no action unit are found for the given id
     * @throws RuntimeException             If the repository method returns a RuntimeException
     */
    public ActionUnit findById(long id) {
        try {
            return actionUnitRepository.findById(id).orElseThrow(() -> new ActionUnitNotFoundException("ActionUnit not found with ID: " + id));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

}
