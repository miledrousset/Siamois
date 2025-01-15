package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.history.RecordingUnitStudyHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface RecordingUnitStudyHistoryRepository extends CrudRepository<RecordingUnitStudyHist, Long>, HistoryEntries {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM history_recording_unit_study WHERE fk_author_id = :personId AND update_time BETWEEN :start AND :end"
    )
    List<RecordingUnitStudyHist> findAllOfUserBetween(OffsetDateTime start, OffsetDateTime end, Long personId);

}
