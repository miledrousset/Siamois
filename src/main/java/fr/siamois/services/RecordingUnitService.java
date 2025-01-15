package fr.siamois.services;

import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitStudyRepository;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.ActionUnit;

import fr.siamois.models.ark.Ark;
import fr.siamois.models.ark.ArkServer;
import fr.siamois.models.exceptions.FailedRecordingUnitSaveException;
import fr.siamois.models.exceptions.RecordingUnitNotFoundException;
import fr.siamois.models.recordingunit.RecordingUnit;

import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.services.ark.ArkGenerator;
import fr.siamois.services.vocabulary.FieldService;
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
    private final FieldService fieldService;


    public RecordingUnitService(RecordingUnitRepository recordingUnitRepository, ArkServerRepository arkServerRepository, RecordingUnitStudyRepository recordingUnitStudyRepository, FieldService fieldService) {
        this.recordingUnitRepository = recordingUnitRepository;
        this.arkServerRepository = arkServerRepository;
        this.fieldService = fieldService;
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
    public RecordingUnit save(RecordingUnit recordingUnit, Vocabulary vocabulary, ConceptFieldDTO
            typeConceptFieldDTO) {

        try {
            // Generate ARK if the recording unit does not have any
            if (recordingUnit.getArk() == null) {

                ArkServer localServer = arkServerRepository.findLocalServer().orElseThrow(() -> new IllegalStateException("No local server found"));
                Ark ark = new Ark();
                ark.setArkServer(
                        arkServerRepository.findLocalServer().orElseThrow(() -> new IllegalStateException("No local server found"))
                );
                ark.setArkId(ArkGenerator.generateArk());
                recordingUnit.setArk(ark);
            }

            // Add concept
            Concept type = fieldService.saveOrGetConceptFromDto(vocabulary, typeConceptFieldDTO);
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


