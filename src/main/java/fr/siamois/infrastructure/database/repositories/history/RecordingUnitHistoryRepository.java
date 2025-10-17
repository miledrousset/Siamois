package fr.siamois.infrastructure.database.repositories.history;

import fr.siamois.domain.models.history.RecordingUnitHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Deprecated
public interface RecordingUnitHistoryRepository extends CrudRepository<RecordingUnitHist, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM history_recording_unit WHERE recording_unit_id = :tableId ORDER BY update_time DESC"
    )
    List<RecordingUnitHist> findAllByTableId(Long tableId);

}
