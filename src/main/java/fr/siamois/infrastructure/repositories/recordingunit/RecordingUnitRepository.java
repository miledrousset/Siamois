package fr.siamois.infrastructure.repositories.recordingunit;



import fr.siamois.infrastructure.repositories.history.TraceableEntries;
import fr.siamois.models.recordingunit.RecordingUnit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface RecordingUnitRepository extends CrudRepository<RecordingUnit, Long>, TraceableEntries {

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

    @Query(
            nativeQuery = true,
            value = "SELECT ru.* FROM recording_unit ru WHERE fk_author_id = :author AND creation_time BETWEEN :start AND :end"
    )
    List<RecordingUnit> findAllCreatedBetweenByUser(@Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, @Param("author") Long personId);

    //   Optional<RecordingUnit> findById(long id);

//    todo:  @Query(
//            nativeQuery = true,
//            value = "SELECT * FROM recording_unit ru JOIN stratigraphic_relationship sr on ru.recording_unit_id = ruh.fk_parent_id WHERE ruh.fk_child_id = :recordingUnit"
//    )
//    List<RecordingUnit> findAllStratigraphicRelationshipsOfRecordingUnit(@Param("recordingUnit") RecordingUnit recordingUnit);

}
