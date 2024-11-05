package fr.siamois.repositories;

import fr.siamois.models.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordingUnitRepository extends CrudRepository<RecordingUnit, Integer> {

    @Query(
            nativeQuery = true,
            value = "SELECT ru.* FROM recording_unit ru JOIN recording_unit_hierarchy ruh on ru.recording_unit_id = ruh.fk_child_id WHERE ruh.fk_parent_id = :recordingUnit"
    )
    List<RecordingUnit> findAllChildrenOfRecordingUnit(@Param("recordingUnit") RecordingUnit recordingUnit);

    @Query(
            nativeQuery = true,
            value = "SELECT ru.* FROM recording_unit ru JOIN recording_unit_hierarchy ruh on ru.recording_unit_id = ruh.fk_parent_id WHERE ruh.fk_child_id = :recordingUnit"
    )
    List<RecordingUnit> findAllParentsOfRecordingUnit(@Param("recordingUnit") RecordingUnit recordingUnit);


    List<RecordingUnit> findAllBySpatialUnitId(int spatialUnitId);

//    todo:  @Query(
//            nativeQuery = true,
//            value = "SELECT * FROM recording_unit ru JOIN stratigraphic_relationship sr on ru.recording_unit_id = ruh.fk_parent_id WHERE ruh.fk_child_id = :recordingUnit"
//    )
//    List<RecordingUnit> findAllStratigraphicRelationshipsOfRecordingUnit(@Param("recordingUnit") RecordingUnit recordingUnit);

}
