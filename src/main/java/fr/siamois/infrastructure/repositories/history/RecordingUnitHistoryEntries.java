package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.history.RecordingUnitHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface RecordingUnitHistoryEntries extends CrudRepository<RecordingUnitHist, Long>, HistoryEntries {

    @Query(
            nativeQuery = true,
            value = "SELECT hru.* FROM history_recording_unit hru WHERE fk_author_id = :personId AND update_time BETWEEN :start AND :end"
    )
    List<RecordingUnitHist> findAllOfUserBetween(OffsetDateTime start, OffsetDateTime end, Long personId);

}
