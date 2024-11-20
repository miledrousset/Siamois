package fr.siamois.infrastructure.repositories.recordingunit;

import fr.siamois.models.recordingunit.RecordingUnitStudy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordingUnitStudyRepository extends CrudRepository<RecordingUnitStudy, Long> {


}

