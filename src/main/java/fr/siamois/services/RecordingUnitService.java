package fr.siamois.services;

import fr.siamois.models.RecordingUnit;
import fr.siamois.models.SpatialUnit;

import fr.siamois.repositories.RecordingUnitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RecordingUnitService {

    @Autowired
    RecordingUnitRepository recordingUnitRepository;

    /**
     * Find all the recording units from a spatial unit
     *
     * @return The List of RecordingUnit
     * @throws RuntimeException If the repository method throws an Exception
     */
    public List<RecordingUnit> findAllBySpatialUnitId(SpatialUnit spatialUnit)   {
        return recordingUnitRepository.findAllBySpatialUnitId(spatialUnit.getId());
    }

}
