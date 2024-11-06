package fr.siamois.services;

import fr.siamois.exceptions.SpatialUnitNotFoundException;
import fr.siamois.models.SpatialUnit;
import fr.siamois.repositories.SpatialUnitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SpatialUnitService {

    @Autowired
    SpatialUnitRepository spatialUnitRepository;

    public List<SpatialUnit> findAllWithoutParents() {
        return spatialUnitRepository.findAllWithoutParents();
    }

    public List<SpatialUnit> findAllChildOfSpatialUnit(SpatialUnit spatialUnit) {

        return spatialUnitRepository.findAllChildOfSpatialUnit(spatialUnit.getId());
    }

    /**
     * Find a spatial unit by its ID
     *
     * @param id The ID of the spatial unit
     * @return SpatialUnit
     * @throws SpatialUnitNotFoundException If no spatial unit are found for the given id
     * @throws RuntimeException If the repository method returns a RuntimeException
     */
    public SpatialUnit findById(int id) {
        try {
            return spatialUnitRepository.findById(id).orElseThrow(() -> new SpatialUnitNotFoundException("SpatialUnit not found with ID: " + id));
        } catch (SpatialUnitNotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}
