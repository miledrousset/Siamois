package fr.siamois.infrastructure.repositories.recordingunit;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnitStudy;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordingUnitStudyRepository extends CrudRepository<RecordingUnitStudy, Long> {

    List<RecordingUnitStudy> findAllByArkIsNullAndCreatedByInstitution(@NotNull Institution createdByInstitution);
}

