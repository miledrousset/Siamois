package fr.siamois.services.actionunit;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.repositories.actionunit.ActionUnitRepository;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.models.actionunit.ActionCode;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.Team;
import fr.siamois.models.exceptions.ActionUnitNotFoundException;
import fr.siamois.models.exceptions.FailedRecordingUnitSaveException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.vocabulary.FieldService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ActionUnitService {

    private final ActionUnitRepository actionUnitRepository;
    private final ArkServerRepository arkServerRepository;
    private final FieldService fieldService;

    public ActionUnitService(ActionUnitRepository actionUnitRepository, ArkServerRepository arkServerRepository, FieldService fieldService) {
        this.actionUnitRepository = actionUnitRepository;
        this.arkServerRepository = arkServerRepository;
        this.fieldService = fieldService;
    }

    public List<ActionUnit> findAllBySpatialUnitId(SpatialUnit spatialUnit)   {
        return actionUnitRepository.findAllBySpatialUnitId(spatialUnit.getId());
    }

    public List<ActionUnit> findAllBySpatialUnitIdOfTeam(SpatialUnit spatialUnit, Team team)   {
        return actionUnitRepository.findAllBySpatialUnitIdOfTeam(spatialUnit.getId(), team.getId());
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
    public ActionUnit save(ActionUnit actionUnit, Vocabulary vocabulary, ConceptFieldDTO
            typeConceptFieldDTO) {

        try {

            // Add concept
            Concept type = fieldService.saveOrGetConceptFromDto(vocabulary, typeConceptFieldDTO);
            actionUnit.setType(type);

            return actionUnitRepository.save(actionUnit);
        } catch (RuntimeException e) {
            throw new FailedRecordingUnitSaveException(e.getMessage());
        }
    }

    @Transactional
    public ActionUnit save(ActionUnit actionUnit, List<ActionCode> secondaryActionCodes) {

        try {

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

            actionUnit.setSecondaryActionCodes(currentSecondaryActionCodes);

            return actionUnitRepository.save(actionUnit);
        } catch (RuntimeException e) {
            throw new FailedRecordingUnitSaveException(e.getMessage());
        }
    }

}
