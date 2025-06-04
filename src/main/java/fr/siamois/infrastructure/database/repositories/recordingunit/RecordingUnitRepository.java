package fr.siamois.infrastructure.database.repositories.recordingunit;


import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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
                    "WHERE ru.fk_spatial_unit_id = :spatialUnitId"
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

    @Query(
            nativeQuery = true,
            value = "WITH ranked_labels AS ( " +
                    "    SELECT " +
                    "        l.fk_concept_id, " +
                    "        l.label_value, " +
                    "        l.lang_code, " +
                    "        ROW_NUMBER() OVER ( " +
                    "            PARTITION BY l.fk_concept_id " +
                    "            ORDER BY  " +
                    "                CASE  " +
                    "                    WHEN l.lang_code = :langCode THEN 1 " +
                    "                    WHEN l.lang_code = 'en' THEN 2 " +
                    "                    ELSE 3 " +
                    "                END " +
                    "        ) AS rank " +
                    "    FROM label l " +
                    ") " +
                    "SELECT " +
                    "    ru.*, " +
                    "    rl.label_value AS c_label " +
                    "FROM recording_unit ru " +
                    "LEFT JOIN concept c ON ru.fk_type = c.concept_id " +
                    "LEFT JOIN ranked_labels rl ON c.concept_id = rl.fk_concept_id AND rl.rank = 1 " +
                    "WHERE ru.fk_institution_id = :institutionId " +
                    "  AND (CAST(:fullIdentifier AS TEXT) IS NULL OR LOWER(ru.full_identifier) LIKE LOWER(CONCAT('%', CAST(:fullIdentifier AS TEXT), '%'))) " +
                    "  AND (CAST(:categoryIds AS BIGINT[]) IS NULL OR ru.fk_type IN (:categoryIds)) " +
                    "  AND (CAST(:global AS TEXT) IS NULL OR LOWER(ru.full_identifier) LIKE LOWER(CONCAT('%', CAST(:global AS TEXT), '%'))  " +
                    "                                     OR LOWER(rl.label_value) LIKE LOWER(CONCAT('%', CAST(:global AS TEXT), '%'))) ",
            countQuery = "WITH ranked_labels AS ( " +
                    "    SELECT " +
                    "        l.fk_concept_id, " +
                    "        l.label_value, " +
                    "        l.lang_code, " +
                    "        ROW_NUMBER() OVER ( " +
                    "            PARTITION BY l.fk_concept_id " +
                    "            ORDER BY  " +
                    "                CASE  " +
                    "                    WHEN l.lang_code = :langCode THEN 1 " +
                    "                    WHEN l.lang_code = 'en' THEN 2 " +
                    "                    ELSE 3 " +
                    "                END " +
                    "        ) AS rank " +
                    "    FROM label l " +
                    ") " +
                    "SELECT " +
                    "    ru.*, " +
                    "    rl.label_value AS c_label " +
                    "FROM recording_unit ru " +
                    "LEFT JOIN concept c ON ru.fk_type = c.concept_id " +
                    "LEFT JOIN ranked_labels rl ON c.concept_id = rl.fk_concept_id AND rl.rank = 1 " +
                    "WHERE ru.fk_institution_id = :institutionId " +
                    "  AND (CAST(:fullIdentifier AS TEXT) IS NULL OR LOWER(ru.full_identifier) LIKE LOWER(CONCAT('%', CAST(:fullIdentifier AS TEXT), '%'))) " +
                    "  AND (CAST(:categoryIds AS BIGINT[]) IS NULL OR ru.fk_type IN (:categoryIds)) " +
                    "  AND (CAST(:global AS TEXT) IS NULL OR LOWER(ru.full_identifier) LIKE LOWER(CONCAT('%', CAST(:global AS TEXT), '%'))  " +
                    "                                     OR LOWER(rl.label_value) LIKE LOWER(CONCAT('%', CAST(:global AS TEXT), '%'))) "
    )
    Page<RecordingUnit> findAllByInstitutionAndByFullIdentifierContainingAndByCategoriesAndByGlobalContaining(
            @Param("institutionId") Long institutionId,
            @Param("fullIdentifier") String fullIdentifier,
            @Param("categoryIds") Long[] categoryIds,
            @Param("global") String global,
            @Param("langCode") String langCode,
            Pageable pageable);
}
