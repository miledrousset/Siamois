package fr.siamois.infrastructure.repositories.history;

import fr.siamois.models.history.SpatialUnitHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface SpatialUnitHistoryRepository extends CrudRepository<SpatialUnitHist, Long>, HistoryEntries {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM history_spatial_unit WHERE fk_author_id = :personId AND update_time BETWEEN :start AND :end"
    )
    List<SpatialUnitHist> findAllOfUserBetween(OffsetDateTime start, OffsetDateTime end, Long personId);

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM history_spatial_unit WHERE spatial_unit_id = :tableId ORDER BY update_time DESC"
    )
    List<SpatialUnitHist> findAllByTableId(Long tableId);

}
