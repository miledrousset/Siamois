package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.history.ActionUnitHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface ActionUnitHistoryEntries extends CrudRepository<ActionUnitHist, Long>, HistoryEntries {

    @Query(
            nativeQuery = true,
            value = "SELECT hau.* FROM history_action_unit hau WHERE fk_author_id = :personId AND update_time BETWEEN :start AND :end"
    )
    List<ActionUnitHist> findAllOfUserBetween(OffsetDateTime start, OffsetDateTime end, Long personId);

}
