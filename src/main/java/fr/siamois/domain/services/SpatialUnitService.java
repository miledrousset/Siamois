package fr.siamois.domain.services;

import fr.siamois.infrastructure.repositories.SpatialUnitRepository;
import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.exceptions.SpatialUnitAlreadyExistsException;
import fr.siamois.domain.models.exceptions.SpatialUnitNotFoundException;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.ark.ArkService;
import fr.siamois.domain.services.vocabulary.ConceptService;
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
public class SpatialUnitService implements ArkEntityService {

    private final SpatialUnitRepository spatialUnitRepository;
    private final ConceptService conceptService;
    private final ArkService arkService;
    private final InstitutionService institutionService;

    public SpatialUnitService(SpatialUnitRepository spatialUnitRepository, ConceptService conceptService, ArkService arkService, InstitutionService institutionService) {
        this.spatialUnitRepository = spatialUnitRepository;
        this.conceptService = conceptService;
        this.arkService = arkService;
        this.institutionService = institutionService;
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

        type = conceptService.saveOrGetConcept(type);

        SpatialUnit spatialUnit = new SpatialUnit();
        spatialUnit.setName(name);
        spatialUnit.setCreatedByInstitution(info.getInstitution());
        spatialUnit.setAuthor(info.getUser());
        spatialUnit.setCategory(type);
        spatialUnit.setCreationTime(OffsetDateTime.now(ZoneId.systemDefault()));

        InstitutionSettings settings = institutionService.createOrGetSettingsOf(info.getInstitution());
        if (settings.hasEnabledArkConfig()) {
            Ark ark = arkService.generateAndSave(settings);
            spatialUnit.setArk(ark);
        }

        spatialUnit = spatialUnitRepository.save(spatialUnit);

        for (SpatialUnit parent : parents) {
            spatialUnitRepository.addParentToSpatialUnit(spatialUnit.getId(), parent.getId());
        }

        return spatialUnit;
    }

    public Optional<SpatialUnit> findByArk(Ark ark) {
        return spatialUnitRepository.findByArk(ark);
    }

    @Override
    public List<SpatialUnit> findWithoutArk(Institution institution) {
        return spatialUnitRepository.findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Override
    public ArkEntity save(ArkEntity toSave) {
        return spatialUnitRepository.save((SpatialUnit) toSave);
    }
}
