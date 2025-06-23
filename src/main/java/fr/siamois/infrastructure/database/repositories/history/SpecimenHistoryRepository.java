package fr.siamois.infrastructure.database.repositories.history;

import fr.siamois.domain.models.history.RecordingUnitHist;
import fr.siamois.domain.models.history.SpecimenHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpecimenHistoryRepository extends CrudRepository<SpecimenHist, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM history_specimen WHERE specimen_id = :tableId ORDER BY update_time DESC"
    )
    List<SpecimenHist> findAllByTableId(Long tableId);

}
