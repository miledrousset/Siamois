package fr.siamois.infrastructure.database.repositories.history;

import fr.siamois.domain.models.history.SpatialUnitHist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@Deprecated
public interface SpatialUnitHistoryRepository extends CrudRepository<SpatialUnitHist, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM history_spatial_unit WHERE spatial_unit_id = :tableId ORDER BY update_time DESC"
    )
    List<SpatialUnitHist> findAllByTableId(Long tableId);

}
