package fr.siamois.infrastructure.database.repositories.recordingunit;


import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordingUnitRepository extends CrudRepository<RecordingUnit, Long> {

    /**
     * @param spatialUnitId - The ID of the spatial unit
     * @return List of recording units
     */
    @Query(
            nativeQuery = true,
            value = "SELECT ru.* FROM recording_unit ru " +
                    "JOIN action_unit au ON ru.fk_action_unit_id = au.action_unit_id " +
                    "JOIN spatial_unit su ON au.fk_spatial_unit_id = su.spatial_unit_id " +
                    "WHERE su.spatial_unit_id = :spatialUnitId"
    )
    List<RecordingUnit> findAllBySpatialUnitId(Long spatialUnitId);

    List<RecordingUnit> findAllByActionUnit(ActionUnit actionUnit);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO stratigraphic_relationship(fk_recording_unit_1_id, fk_recording_unit_2_id, fk_relationship_concept_id) " +
                    "VALUES (:recordingUnitId1, :recordingUnitId2, :conceptId)"
    )
    void saveStratigraphicRelationship(Long recordingUnitId1, Long recordingUnitId2, Long conceptId);

    /**
     * Returns the maximum identifier given to a recording unit in the context of an action unit
     *
     * @return The max identifier
     */
    @Query(
            nativeQuery = true,
            value = "SELECT MAX(ru.identifier) " +
                    "FROM recording_unit ru where ru.fk_action_unit_id = :actionUnitId"
    )
    Integer findMaxUsedIdentifierByAction(Long actionUnitId);


    @Query(
            nativeQuery = true,
            value = "SELECT ru.* FROM recording_unit ru " +
                    "WHERE ru.fk_ark_id IS NULL AND ru.fk_institution_id = :institutionId"
    )
    List<RecordingUnit> findAllWithoutArkOfInstitution(Long institutionId);

    long countByCreatedByInstitution(Institution institution);
}
