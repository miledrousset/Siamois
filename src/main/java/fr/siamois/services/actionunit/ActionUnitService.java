package fr.siamois.services.actionunit;

import fr.siamois.infrastructure.repositories.actionunit.ActionCodeRepository;
import fr.siamois.infrastructure.repositories.actionunit.ActionUnitRepository;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.models.actionunit.ActionCode;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.exceptions.FailedActionUnitSaveException;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.UserInfo;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.ark.ArkServer;
import fr.siamois.models.exceptions.ActionUnitNotFoundException;
import fr.siamois.models.exceptions.FailedRecordingUnitSaveException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.ark.ArkGenerator;
import fr.siamois.services.vocabulary.ConceptService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class ActionUnitService {

    private final ActionUnitRepository actionUnitRepository;
    private final ArkServerRepository arkServerRepository;
    private final ConceptService conceptService;
    private final ActionCodeRepository actionCodeRepository;

    public ActionUnitService(ActionUnitRepository actionUnitRepository,
                             ArkServerRepository arkServerRepository,
                             ConceptService conceptService, ActionCodeRepository actionCodeRepository) {
        this.actionUnitRepository = actionUnitRepository;
        this.arkServerRepository = arkServerRepository;
        this.conceptService = conceptService;
        this.actionCodeRepository = actionCodeRepository;
    }

    public List<ActionUnit> findAllBySpatialUnitId(SpatialUnit spatialUnit)   {
        return actionUnitRepository.findAllBySpatialUnitId(spatialUnit.getId());
    }

    /**
     * Find an action unit by its ID
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

    @Transactional
    public ActionUnit save(UserInfo info, ActionUnit actionUnit, Concept typeConcept) {

        try {

            // Add concept
            Concept type = conceptService.saveOrGetConcept(typeConcept);
            actionUnit.setType(type);

            actionUnit.setAuthor(info.getUser());
            actionUnit.setCreatedByInstitution(info.getInstitution());

            return actionUnitRepository.save(actionUnit);
        } catch (RuntimeException e) {
            throw new FailedRecordingUnitSaveException(e.getMessage());
        }
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
            ActionCode primaryActionCode = actionUnit.getPrimaryActionCode();
            // Is the action code concept already in DB ?
            actionUnit.getPrimaryActionCode().setType(conceptService.saveOrGetConcept(actionUnit.getPrimaryActionCode().getType()));
            // Saving or retrieving primary action code
            actionUnit.setPrimaryActionCode(saveOrGetActionCode(actionUnit.getPrimaryActionCode()));

            // ------------------ Handle secondary codes
            // Get the old version of the actionUnit
            ActionUnit currentVersion = actionUnitRepository.findById(actionUnit.getId()).get();

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
            return save(info, actionUnit, actionUnit.getType());

        } catch (RuntimeException e) {
            throw new FailedActionUnitSaveException(e.getMessage());
        }
    }

}
