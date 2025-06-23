package fr.siamois.infrastructure.database.repositories.history;

import fr.siamois.domain.models.history.ActionUnitHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActionUnitHistoryRepository extends CrudRepository<ActionUnitHist, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM history_action_unit WHERE action_unit_id = :tableId ORDER BY update_time DESC"
    )
    List<ActionUnitHist> findAllByTableId(Long tableId);

}
