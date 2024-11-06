package fr.siamois.services;

import fr.siamois.exceptions.SpatialUnit.SpatialUnitNotFoundException;
import fr.siamois.models.SpatialUnit;
import fr.siamois.repositories.SpatialUnitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Slf4j
@Service
public class SpatialUnitService {

    @Autowired
    SpatialUnitRepository spatialUnitRepository;

    public List<SpatialUnit> findAllWithoutParents() {
        return spatialUnitRepository.findAllWithoutParents();
    }

    /**
     * Find all the spatial unit not having another spatial unit as parent
     *
     * @return The List of SpatialUnit
     * @throws RuntimeException             If the repository method throws a Exception
     */
    public List<SpatialUnit> findAllChildOfSpatialUnit(SpatialUnit spatialUnit) {
        try {
            return spatialUnitRepository.findAllChildOfSpatialUnit(spatialUnit.getId());
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * Find a spatial unit by its ID
     *
     * @param id The ID of the spatial unit
     * @return The SpatialUnit having the given ID
     * @throws SpatialUnitNotFoundException If no spatial unit are found for the given id
     * @throws RuntimeException             If the repository method returns a RuntimeException
     */
    public SpatialUnit findById(int id) {
        try {
            return spatialUnitRepository.findById(id).orElseThrow(() -> new SpatialUnitNotFoundException("SpatialUnit not found with ID: " + id));
        } catch (RuntimeException e) {
            throw e;
        }
    }

}
