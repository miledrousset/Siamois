package fr.siamois.services;

import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.ark.ArkServer;
import fr.siamois.models.exceptions.FailedRecordingUnitSaveException;
import fr.siamois.models.exceptions.RecordingUnitNotFoundException;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.spatialunit.SpatialUnit;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.services.vocabulary.ConceptService;
import fr.siamois.utils.ArkGeneratorUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to manage RecordingUnit
 *
 * @author Grégory Bliault
 */
@Slf4j
@Service
@Transactional
public class RecordingUnitService {

    private final RecordingUnitRepository recordingUnitRepository;
    private final ArkServerRepository arkServerRepository;
    private final ConceptService conceptService;

    public RecordingUnitService(RecordingUnitRepository recordingUnitRepository,
                                ArkServerRepository arkServerRepository,
                                ConceptService conceptService) {
        this.recordingUnitRepository = recordingUnitRepository;
        this.arkServerRepository = arkServerRepository;
        this.conceptService = conceptService;
    }


    /**
     * Find all the recording units from a spatial unit
     *
     * @return The List of RecordingUnit
     * @throws RuntimeException If the repository method throws an Exception
     */
    public List<RecordingUnit> findAllBySpatialUnit(SpatialUnit spatialUnit) {
        return recordingUnitRepository.findAllBySpatialUnitId(spatialUnit.getId());
    }

    /**
     * Find all the recording units from an action unit
     *
     * @return The List of RecordingUnit
     * @throws RuntimeException If the repository method throws an Exception
     */
    public List<RecordingUnit> findAllByActionUnit(ActionUnit actionUnit) {
        return recordingUnitRepository.findAllByActionUnit(actionUnit);
    }

    @Transactional
    public RecordingUnit save(RecordingUnit recordingUnit, Concept concept) {

        try {
            // Generate ARK if the recording unit does not have any
            if (recordingUnit.getArk() == null) {

                ArkServer localServer = arkServerRepository.findLocalServer().orElseThrow(() -> new IllegalStateException("No local server found"));
                Ark ark = new Ark();
                ark.setArkServer(localServer);
                ark.setArkId(ArkGeneratorUtils.generateArk());
                recordingUnit.setArk(ark);
            }

            // Generate unique identifier if not present
            if (recordingUnit.getFullIdentifier() == null) {
                if (recordingUnit.getIdentifier() == null) {
                    // Generate next identifier
                    Integer currentMaxIdentifier = recordingUnitRepository.findMaxUsedIdentifierByAction(recordingUnit.getActionUnit().getId());
                    Integer nextIdentifier = (currentMaxIdentifier == null) ? recordingUnit.getActionUnit().getMaxRecordingUnitCode() : currentMaxIdentifier + 1;
                    if (nextIdentifier > recordingUnit.getActionUnit().getMaxRecordingUnitCode()) {
                        throw new RuntimeException("Max recording unit code reached; Please ask administrator to increase the range");
                    }
                    recordingUnit.setIdentifier(nextIdentifier);
                }
                // Set full identifier
                recordingUnit.setFullIdentifier(recordingUnit.displayFullIdentifier());
            }

            // Add concept
            Concept type = conceptService.saveOrGetConcept(concept);
            recordingUnit.setType(type);

            return recordingUnitRepository.save(recordingUnit);
        } catch (RuntimeException e) {
            throw new FailedRecordingUnitSaveException(e.getMessage());
        }
    }

    /**
     * Find a recording unit by its ID
     *
     * @param id The ID of the recording unit
     * @return The RecordingUnit having the given ID
     * @throws RecordingUnitNotFoundException If no recording unit are found for the given id
     * @throws RuntimeException               If the repository method returns a RuntimeException
     */
    public RecordingUnit findById(long id) {
        try {
            return recordingUnitRepository.findById(id).orElseThrow(() -> new RecordingUnitNotFoundException("RecordingUnit not found with ID: " + id));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

}


