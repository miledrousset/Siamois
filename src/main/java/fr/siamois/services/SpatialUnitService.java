package fr.siamois.services;

import fr.siamois.infrastructure.repositories.SpatialUnitRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.UserInfo;
import fr.siamois.models.exceptions.SpatialUnitAlreadyExistsException;
import fr.siamois.models.exceptions.SpatialUnitNotFoundException;
import fr.siamois.models.history.SpatialUnitHist;
import fr.siamois.models.vocabulary.Concept;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Service to manage SpatialUnit
 *
 * @author Grégory Bliault
 */
@Slf4j
@Service
public class SpatialUnitService {

    private final SpatialUnitRepository spatialUnitRepository;

    public SpatialUnitService(SpatialUnitRepository spatialUnitRepository) {
        this.spatialUnitRepository = spatialUnitRepository;
    }

    /**
     * Find all the spatial unit not having any spatial unit as parent
     *
     * @return The List of SpatialUnit
     * @throws RuntimeException             If the repository method throws an Exception
     */
    public List<SpatialUnit> findAllWithoutParents() {
        return spatialUnitRepository.findAllWithoutParents();
    }

    /**
     * Find all the children of a spatial unit
     *
     * @return The List of SpatialUnit
     * @throws RuntimeException             If the repository method throws an Exception
     */
    public List<SpatialUnit> findAllChildOfSpatialUnit(SpatialUnit spatialUnit) {
        return spatialUnitRepository.findAllChildOfSpatialUnit(spatialUnit.getId());
    }

    public List<SpatialUnit> findAllParentsOfSpatialUnit(SpatialUnit spatialUnit) {
        return spatialUnitRepository.findAllParentsOfSpatialUnit(spatialUnit.getId());
    }

    /**
     * Find a spatial unit by its ID
     *
     * @param id The ID of the spatial unit
     * @return The SpatialUnit having the given ID
     * @throws SpatialUnitNotFoundException If no spatial unit are found for the given id
     * @throws RuntimeException             If the repository method returns a RuntimeException
     */
    public SpatialUnit findById(long id) {
        try {
            return spatialUnitRepository.findById(id).orElseThrow(() -> new SpatialUnitNotFoundException("SpatialUnit not found with ID: " + id));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public void restore(SpatialUnitHist history) {
        SpatialUnit spatialUnit = history.createOriginal(SpatialUnit.class);
        log.trace(spatialUnit.toString());
        spatialUnitRepository.save(spatialUnit);
    }

    public List<SpatialUnit> findAllWithoutParentsOfInstitution(Institution institution) {
        return spatialUnitRepository.findAllWithoutParentsOfInstitution(institution.getId());
    }

    public List<SpatialUnit> findAllOfInstitution(Institution institution) {
        return spatialUnitRepository.findAllOfInstitution(institution.getId());
    }

    public SpatialUnit save(UserInfo info, String name, Concept type, List<SpatialUnit> parents) throws SpatialUnitAlreadyExistsException {
        Optional<SpatialUnit> optSpatialUnit = spatialUnitRepository.findByNameAndInstitution(name, info.getInstitution().getId());
        if (optSpatialUnit.isPresent())
            throw new SpatialUnitAlreadyExistsException(
                    String.format("Spatial Unit with name %s already exist in institution %s", name, info.getInstitution().getName()));

        SpatialUnit spatialUnit = new SpatialUnit();
        spatialUnit.setName(name);
        spatialUnit.setCreatedByInstitution(info.getInstitution());
        spatialUnit.setAuthor(info.getUser());
        spatialUnit.setCategory(type);
        spatialUnit.setCreationTime(OffsetDateTime.now(ZoneId.systemDefault()));

        spatialUnit = spatialUnitRepository.save(spatialUnit);

        for (SpatialUnit parent : parents) {
            spatialUnitRepository.addParentToSpatialUnit(spatialUnit.getId(), parent.getId());
        }

        return null;
    }

}
