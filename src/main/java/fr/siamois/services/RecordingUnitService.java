package fr.siamois.services;

import fr.siamois.infrastructure.repositories.ark.ArkRepository;
import fr.siamois.infrastructure.repositories.ark.ArkServerRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitStudyRepository;
import fr.siamois.models.SpatialUnit;

import fr.siamois.models.ark.Ark;
import fr.siamois.models.exceptions.ActionUnitNotFoundException;
import fr.siamois.models.exceptions.FailedRecordingUnitSaveException;
import fr.siamois.models.exceptions.RecordingUnitNotFoundException;
import fr.siamois.models.exceptions.SpatialUnitNotFoundException;
import fr.siamois.models.recordingunit.RecordingUnit;

import fr.siamois.services.ark.ArkGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RecordingUnitService {

    private final RecordingUnitRepository recordingUnitRepository;
    private final ArkServerRepository arkServerRepository;
    private final RecordingUnitStudyRepository recordingUnitStudyRepository;

    public RecordingUnitService(RecordingUnitRepository recordingUnitRepository, ArkRepository arkRepository, ArkServerRepository arkServerRepository, RecordingUnitStudyRepository recordingUnitStudyRepository) {
        this.recordingUnitRepository = recordingUnitRepository;
        this.arkServerRepository = arkServerRepository;
        this.recordingUnitStudyRepository = recordingUnitStudyRepository;
    }

    /**
     * Find all the recording units from a spatial unit
     *
     * @return The List of RecordingUnit
     * @throws RuntimeException If the repository method throws an Exception
     */
    public List<RecordingUnit> findAllBySpatialUnit(SpatialUnit spatialUnit)   {
        return recordingUnitRepository.findAllBySpatialUnitId(spatialUnit.getId());
    }


    @Transactional
    public RecordingUnit save(RecordingUnit recordingUnit) {

        try {
            // Generate ARK if the recording unit does not have any
            if(recordingUnit.getArk() == null) {
                // Todo : properly generate ARK using proper ark server
                Ark ark = new Ark();
                ark.setArkServer(arkServerRepository.findArkServerByServerArkUri("http://localhost:8099/siamois").orElse(null));
                ark.setArkId(ArkGenerator.generateArk());
                recordingUnit.setArk(ark);
            }

            return recordingUnitRepository.save(recordingUnit);
        } catch(RuntimeException e) {
            throw new FailedRecordingUnitSaveException(e.getMessage());
        }

    }

    /**
     * Find a recording unit by its ID
     *
     * @param id The ID of the recording unit
     * @return The RecordingUnit having the given ID
     * @throws RecordingUnitNotFoundException If no recording unit are found for the given id
     * @throws RuntimeException             If the repository method returns a RuntimeException
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
