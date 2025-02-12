package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.history.RecordingUnitHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecordingUnitHistoryRepository extends CrudRepository<RecordingUnitHist, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM history_recording_unit WHERE recording_unit_id = :tableId ORDER BY update_time DESC"
    )
    List<RecordingUnitHist> findAllByTableId(Long tableId);

}
