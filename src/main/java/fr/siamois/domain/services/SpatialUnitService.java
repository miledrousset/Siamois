package fr.siamois.domain.services;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.exceptions.spatialunit.SpatialUnitAlreadyExistsException;
import fr.siamois.domain.models.exceptions.spatialunit.SpatialUnitNotFoundException;
import fr.siamois.domain.models.history.SpatialUnitHist;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.settings.InstitutionSettings;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.ark.ArkService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.infrastructure.database.repositories.SpatialUnitRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
     * Find a spatial unit by its ID
     *
     * @param id The ID of the spatial unit
     * @return The SpatialUnit having the given ID
     * @throws SpatialUnitNotFoundException If no spatial unit are found for the given id
     * @throws RuntimeException             If the repository method returns a RuntimeException
     */
    @Transactional(readOnly = true)
    public SpatialUnit findById(long id) {
        try {
            SpatialUnit spatialUnit = spatialUnitRepository.findById(id).orElseThrow(() -> new SpatialUnitNotFoundException("SpatialUnit not found with ID: " + id));
            Hibernate.initialize(spatialUnit);
            return spatialUnit;
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

    private Page<SpatialUnit> initializeSpatialUnitLazyAttributes(Page<SpatialUnit> list) {
        list.forEach(spatialUnit -> {
            Hibernate.initialize(spatialUnit.getRelatedActionUnitList());
            Hibernate.initialize(spatialUnit.getRecordingUnitList());
            Hibernate.initialize(spatialUnit.getChildren());
            Hibernate.initialize(spatialUnit.getParents());
        });

        return list;
    }


    @Transactional(readOnly = true)
    public Page<SpatialUnit> findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
            Long institutionId,
            String name, Long[] categoryIds, Long[] personIds, String global, String langCode, Pageable pageable) {

        Page<SpatialUnit> res = spatialUnitRepository.findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
                institutionId, name, categoryIds, personIds, global, langCode, pageable);

        return initializeSpatialUnitLazyAttributes(res);
    }

    @Transactional(readOnly = true)
    public Page<SpatialUnit> findAllByParentAndByNameContainingAndByCategoriesAndByGlobalContaining(
            SpatialUnit parent,
            String name, Long[] categoryIds, Long[] personIds, String global, String langCode, Pageable pageable) {
        Page<SpatialUnit> res = spatialUnitRepository.findAllByParentAndByNameContainingAndByCategoriesAndByGlobalContaining(
                parent.getId(), name, categoryIds, personIds, global, langCode, pageable);

        return initializeSpatialUnitLazyAttributes(res);
    }

    @Transactional(readOnly = true)
    public Page<SpatialUnit> findAllByChildAndByNameContainingAndByCategoriesAndByGlobalContaining(
            SpatialUnit child,
            String name, Long[] categoryIds, Long[] personIds, String global, String langCode, Pageable pageable) {
        Page<SpatialUnit> res = spatialUnitRepository.findAllByChildAndByNameContainingAndByCategoriesAndByGlobalContaining(
                child.getId(), name, categoryIds, personIds, global, langCode, pageable);

        return initializeSpatialUnitLazyAttributes(res);
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
    @Transactional
    public ArkEntity save(ArkEntity toSave) {

        try {

            SpatialUnit managedSpatialUnit ;
            SpatialUnit spatialUnit = (SpatialUnit) toSave;

            if(spatialUnit.getId() != null) {
                Optional<SpatialUnit> optUnit = spatialUnitRepository.findById(spatialUnit.getId());
                managedSpatialUnit = optUnit.orElseGet(SpatialUnit::new);
            }
            else {
                managedSpatialUnit = new SpatialUnit();
            }

            managedSpatialUnit.setName(spatialUnit.getName());
            managedSpatialUnit.setValidated(spatialUnit.getValidated());
            managedSpatialUnit.setArk(spatialUnit.getArk());
            managedSpatialUnit.setAuthor(spatialUnit.getAuthor());
            managedSpatialUnit.setGeom(spatialUnit.getGeom());
            managedSpatialUnit.setCreatedByInstitution(spatialUnit.getCreatedByInstitution());
            // Add concept
            Concept type = conceptService.saveOrGetConcept(spatialUnit.getCategory());
            managedSpatialUnit.setCategory(type);

            return spatialUnitRepository.save(managedSpatialUnit);

        } catch (RuntimeException e) {
            throw new FailedRecordingUnitSaveException(e.getMessage());
        }
    }


    public long countByInstitution(Institution institution) {
        return spatialUnitRepository.countByCreatedByInstitution(institution);
    }

    public List<SpatialUnit> findAll() {
        List<SpatialUnit> result = new ArrayList<>();
        for (SpatialUnit spatialUnit : spatialUnitRepository.findAll()) {
            result.add(spatialUnit);
        }
        return result;
    }

    public long countChildrenByParentId(Long id) {
        return spatialUnitRepository.countChildrenByParentId(id);
    }

    public long countParentsByChildId(Long id) {
        return spatialUnitRepository.countParentsByChildId(id);
    }
}
