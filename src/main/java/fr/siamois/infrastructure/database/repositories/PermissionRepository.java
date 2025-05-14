package fr.siamois.infrastructure.database.repositories;

import fr.siamois.domain.models.permission.ActionUnitPermission;
import fr.siamois.domain.models.permission.EntityPermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends CrudRepository<EntityPermission, Long> {
    @Query(
            nativeQuery = true,
            name = "SELECT ep.* FROM entity_permission ep WHERE ep.fk_action_unit_id = :actionUnitId"
    )
    List<EntityPermission> findPermissionsOfActionUnitById(Long actionUnitId);

    @Query(
            nativeQuery = true,
            name = "SELECT ep.* FROM entity_permission ep WHERE ep.fk_spatial_unit_id = :spatialUnitId"
    )
    List<EntityPermission> findPermissionsOfSpatialUnitById(Long spatialUnitId);

    @Query(
            nativeQuery = true,
            name = "SELECT ep.* FROM entity_permission ep WHERE ep.fk_recording_unit_id = :recordingUnitId"
    )
    List<EntityPermission> findPermissionsOfRecordingUnit(Long recordingUnitId);
}
