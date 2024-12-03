package fr.siamois.services;

import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitRepository;
import fr.siamois.infrastructure.repositories.recordingunit.RecordingUnitStudyRepository;
import fr.siamois.models.SpatialUnit;

import fr.siamois.models.recordingunit.RecordingUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RecordingUnitService {

    private final RecordingUnitRepository recordingUnitRepository;
    private final RecordingUnitStudyRepository recordingUnitStudyRepository;

    public RecordingUnitService(RecordingUnitRepository recordingUnitRepository, RecordingUnitStudyRepository recordingUnitStudyRepository) {
        this.recordingUnitRepository = recordingUnitRepository;
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

    public RecordingUnit save(RecordingUnit recordingUnit) {
        return recordingUnitRepository.save(recordingUnit);
    }

}
