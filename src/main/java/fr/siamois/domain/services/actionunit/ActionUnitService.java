package fr.siamois.domain.services.actionunit;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.actionunit.ActionCode;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.exceptions.actionunit.ActionUnitNotFoundException;
import fr.siamois.domain.models.exceptions.actionunit.FailedActionUnitSaveException;
import fr.siamois.domain.models.exceptions.actionunit.NullActionUnitIdentifierException;
import fr.siamois.domain.models.exceptions.recordingunit.FailedRecordingUnitSaveException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.services.ArkEntityService;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.infrastructure.database.repositories.actionunit.ActionCodeRepository;
import fr.siamois.infrastructure.database.repositories.actionunit.ActionUnitRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class ActionUnitService implements ArkEntityService {

    private final ActionUnitRepository actionUnitRepository;
    private final ConceptService conceptService;
    private final ActionCodeRepository actionCodeRepository;

    public ActionUnitService(ActionUnitRepository actionUnitRepository,
                             ConceptService conceptService, ActionCodeRepository actionCodeRepository) {
        this.actionUnitRepository = actionUnitRepository;
        this.conceptService = conceptService;
        this.actionCodeRepository = actionCodeRepository;
    }


    @Transactional(readOnly = true)
    public Page<ActionUnit> findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
            Long institutionId,
            String name, Long[] categoryIds, Long[] personIds, String global, String langCode, Pageable pageable) {

        Page<ActionUnit> res = actionUnitRepository.findAllByInstitutionAndByNameContainingAndByCategoriesAndByGlobalContaining(
                institutionId, name, categoryIds, personIds, global, langCode, pageable);

        //wireChildrenAndParents(res.getContent());  // Load and attach spatial hierarchy relationships


        // load related actions
        res.forEach(actionUnit -> {
            Hibernate.initialize(actionUnit.getSpatialContext());
            Hibernate.initialize(actionUnit.getRecordingUnitList());
            Hibernate.initialize(actionUnit.getParents());
            Hibernate.initialize(actionUnit.getChildren());

        });


        return res;
    }

    @Transactional(readOnly = true)
    public Page<ActionUnit> findAllByInstitutionAndBySpatialUnitAndByNameContainingAndByCategoriesAndByGlobalContaining(
            Long institutionId, Long spatialUnitId,
            String name, Long[] categoryIds, Long[] personIds, String global, String langCode, Pageable pageable) {

        Page<ActionUnit> res = actionUnitRepository.findAllByInstitutionAndBySpatialUnitAndByNameContainingAndByCategoriesAndByGlobalContaining(
                institutionId, spatialUnitId, name, categoryIds, personIds, global, langCode, pageable);

        //wireChildrenAndParents(res.getContent());  // Load and attach spatial hierarchy relationships


        // load related actions
        res.forEach(actionUnit -> {
            Hibernate.initialize(actionUnit.getSpatialContext());
            Hibernate.initialize(actionUnit.getRecordingUnitList());
            Hibernate.initialize(actionUnit.getParents());
            Hibernate.initialize(actionUnit.getChildren());

        });


        return res;
    }

    /**
     * Find an action unit by its ID
     *
     * @param id The ID of the action unit
     * @return The ActionUnit having the given ID
     * @throws ActionUnitNotFoundException If no action unit are found for the given id
     * @throws RuntimeException             If the repository method returns a RuntimeException
     */
    @Transactional(readOnly = true)
    public ActionUnit findById(long id) {
        try {
            return actionUnitRepository.findById(id).orElseThrow(() -> new ActionUnitNotFoundException("ActionUnit not found with ID: " + id));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }


    public ActionUnit saveNotTransactional(UserInfo info, ActionUnit actionUnit, Concept typeConcept) {

        try {

            actionUnit.setCreatedByInstitution(info.getInstitution());

            // Generate unique identifier if not presents
            if (actionUnit.getFullIdentifier() == null) {
                if (actionUnit.getIdentifier() == null) {
                    throw new NullActionUnitIdentifierException("ActionUnit identifier must be set");
                }
                // Set full identifier
                actionUnit.setFullIdentifier(actionUnit.displayFullIdentifier());
            }

            // Add concept
            Concept type = conceptService.saveOrGetConcept(typeConcept);
            actionUnit.setType(type);

            actionUnit.setAuthor(info.getUser());


            return actionUnitRepository.save(actionUnit);
        } catch (RuntimeException e) {
            throw new FailedRecordingUnitSaveException(e.getMessage());
        }
    }

    @Transactional
    public ActionUnit save(UserInfo info, ActionUnit actionUnit, Concept typeConcept) {
        return saveNotTransactional(info, actionUnit, typeConcept);
    }

    public List<ActionCode> findAllActionCodeByCodeIsContainingIgnoreCase(String query) {
        return actionCodeRepository.findAllByCodeIsContainingIgnoreCase(query);
    }

    private ActionCode saveOrGetActionCode(ActionCode actionCode) {
        Optional<ActionCode> optActionCode = actionCodeRepository.findById(actionCode.getCode()); // We try to get the code
        if (optActionCode.isPresent()) {
            // We test if the type is the same because it's not possible to modify the type of a code already in the system
            if(actionCode.getType().equals(optActionCode.get().getType())) {
                // If the type matches it's fine, we return it.
                return optActionCode.get();
            }
            else {
                throw new FailedActionUnitSaveException("Code exists but type does not match");
            }
        }
        else {
            // SAVE THE CODE
            return actionCodeRepository.save(actionCode);
        }
    }

    @Transactional
    public ActionUnit save(ActionUnit actionUnit, List<ActionCode> secondaryActionCodes, UserInfo info) {

        try {

            // -------------------- Handle the primary action code
            // Is the action code concept already in DB ?
            actionUnit.getPrimaryActionCode().setType(conceptService.saveOrGetConcept(actionUnit.getPrimaryActionCode().getType()));
            // Saving or retrieving primary action code
            actionUnit.setPrimaryActionCode(saveOrGetActionCode(actionUnit.getPrimaryActionCode()));

            // ------------------ Handle secondary codes
            // Get the old version of the actionUnit
            ActionUnit currentVersion = actionUnitRepository.findById(actionUnit.getId()).orElseThrow(IllegalStateException::new);

            // Get the old list of secondaryActionCodes
            Set<ActionCode> currentSecondaryActionCodes = currentVersion.getSecondaryActionCodes();

            // Handle codes
            // 1. Remove the ones that are not linked to the action unit anymore
            currentSecondaryActionCodes.removeIf(actionCode -> !secondaryActionCodes.contains(actionCode));
            // 2. Add the ones that were not present
            secondaryActionCodes.forEach(actionCode -> {
                if (!currentSecondaryActionCodes.contains(actionCode)) {
                    currentSecondaryActionCodes.add(actionCode);
                }
            });

            // Update action code secondary codes set
            actionUnit.setSecondaryActionCodes(new HashSet<>());
            currentSecondaryActionCodes.forEach(actionCode -> {
                actionCode.setType(conceptService.saveOrGetConcept(actionCode.getType()));
                // Saving or retrieving action code
                actionUnit.getSecondaryActionCodes().add(saveOrGetActionCode(actionCode));
            });

            actionUnit.setSecondaryActionCodes(currentSecondaryActionCodes);

            // Saving the action unit
            return saveNotTransactional(info, actionUnit, actionUnit.getType());

        } catch (RuntimeException e) {
            throw new FailedActionUnitSaveException(e.getMessage());
        }
    }

    public Optional<ActionUnit> findByArk(Ark ark) {
        return actionUnitRepository.findByArk(ark);
    }

    @Override
    public List<ActionUnit> findWithoutArk(Institution institution) {
        return actionUnitRepository.findAllByArkIsNullAndCreatedByInstitution(institution);
    }

    @Override
    public ArkEntity save(ArkEntity toSave) {
        return actionUnitRepository.save((ActionUnit) toSave);
    }

    public long countByInstitution(Institution institution) {
        return actionUnitRepository.countByCreatedByInstitution(institution);
    }

    public long countBySpatialContext(SpatialUnit spatialUnit) {
        return actionUnitRepository.countBySpatialContext(spatialUnit.getId());
    }

    public Set<ActionUnit> findAllByInstitution(Institution institution) {
        return actionUnitRepository.findByCreatedByInstitution(institution);
    }
}
