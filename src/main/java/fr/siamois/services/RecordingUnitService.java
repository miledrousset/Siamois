package fr.siamois.services;

import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitStudyRepository;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.recordingunit.RecordingUnit;
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
public class RecordingUnitService {

    private final RecordingUnitRepository recordingUnitRepository;

    public RecordingUnitService(RecordingUnitRepository recordingUnitRepository) {
        this.recordingUnitRepository = recordingUnitRepository;
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

    public RecordingUnit save(RecordingUnit recordingUnit) {
        return recordingUnitRepository.save(recordingUnit);
    }

    public RecordingUnit findById(long id) {
        return recordingUnitRepository.findById(id).orElse(null);
    }

}
