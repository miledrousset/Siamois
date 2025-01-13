package fr.siamois.infrastructure.repositories.recordingunit;



import fr.siamois.models.recordingunit.RecordingUnit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordingUnitRepository extends CrudRepository<RecordingUnit, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT ru.* FROM recording_unit ru JOIN recording_unit_hierarchy ruh on ru.recording_unit_id = ruh.fk_child_id WHERE ruh.fk_parent_id = :recordingUnitId"
    )
    List<RecordingUnit> findAllChildrenOfRecordingUnit(@Param("recordingUnitId") Long recordingUnitId);

    @Query(
            nativeQuery = true,
            value = "SELECT ru.* FROM recording_unit ru JOIN recording_unit_hierarchy ruh on ru.recording_unit_id = ruh.fk_parent_id WHERE ruh.fk_child_id = :recordingUnitId"
    )
    List<RecordingUnit> findAllParentsOfRecordingUnit(@Param("recordingUnitId") Long recordingUnitId);


    /**
     * @param spatialUnitId - The ID of the spatial unit
     * @return List of recording units
     */
    @Query(
            nativeQuery = true,
            value = "SELECT ru.* FROM recording_unit ru " +
                    "JOIN action_unit au ON ru.fk_action_unit_id = au.action_unit_id " +
                    "JOIN spatial_unit su ON au.fk_spatial_unit_id = su.spatial_unit_id "+
                    "WHERE su.spatial_unit_id = :spatialUnitId"
    )
    List<RecordingUnit> findAllBySpatialUnitId(Long spatialUnitId);

    //   Optional<RecordingUnit> findById(long id);

//    todo:  @Query(
//            nativeQuery = true,
//            value = "SELECT * FROM recording_unit ru JOIN stratigraphic_relationship sr on ru.recording_unit_id = ruh.fk_parent_id WHERE ruh.fk_child_id = :recordingUnit"
//    )
//    List<RecordingUnit> findAllStratigraphicRelationshipsOfRecordingUnit(@Param("recordingUnit") RecordingUnit recordingUnit);

}
