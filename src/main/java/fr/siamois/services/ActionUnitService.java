package fr.siamois.services;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.repositories.ActionUnitRepository;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.models.ActionUnit;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.ark.ArkServer;
import fr.siamois.models.exceptions.ActionUnitNotFoundException;
import fr.siamois.models.exceptions.FailedRecordingUnitSaveException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.ark.ArkGenerator;
import fr.siamois.services.vocabulary.FieldService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
            // Generate ARK if the action unit does not have any
            if (actionUnit.getArk() == null) {

                ArkServer localServer = arkServerRepository.findLocalServer().orElseThrow(() -> new IllegalStateException("No local server found"));
                Ark ark = new Ark();
                ark.setArkServer(
                        arkServerRepository.findLocalServer().orElseThrow(() -> new IllegalStateException("No local server found"))
                );
                ark.setArkId(ArkGenerator.generateArk());
                actionUnit.setArk(ark);
            }

            // Add concept
            Concept type = fieldService.saveOrGetConceptFromDto(vocabulary, typeConceptFieldDTO);
            actionUnit.setType(type);

            return actionUnitRepository.save(actionUnit);
        } catch (RuntimeException e) {
            throw new FailedRecordingUnitSaveException(e.getMessage());
        }
    }

}
