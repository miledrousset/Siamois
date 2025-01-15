package fr.siamois.infrastructure.repositories.recordingunit;

import fr.siamois.models.recordingunit.RecordingUnitStudy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface RecordingUnitStudyRepository extends CrudRepository<RecordingUnitStudy, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT rus.* FROM recording_unit_study rus WHERE fk_author_id = :author AND creation_time BETWEEN :start AND :end"
    )
    List<RecordingUnitStudy> findAllCreatedBetweenByUser(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, @Param("author") Long personId);

}

