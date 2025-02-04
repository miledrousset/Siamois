package fr.siamois.infrastructure.repositories.vocabulary;

import fr.siamois.models.vocabulary.Concept;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConceptRepository extends CrudRepository<Concept, Long> {

    @Query(
            nativeQuery = true,
            value = "SELECT c.* FROM concept c JOIN specimen_study_typology sst on c.concept_id = sst.fk_typology_concept_id WHERE sst.fk_specimen_study_id = :actionUnitId"
    )
    List<Concept> findAllTypologiesOfSpecimenStudy(@Param("actionUnitId") Long specimenStudyId);

    @Query(
            nativeQuery = true,
            value = "SELECT c.* FROM concept c JOIN ru_study_typology rust on c.concept_id = rust.fk_typology_concept_id WHERE rust.fk_ru_study_id = :recordingUnitStudyId"
    )
    List<Concept> findAllTypologiesOfRecordingUnitStudy(@Param("recordingUnitStudyId") Long recordingUnitStudyId);

    /**
     * Find a concept by its arkId.
     * @param arkId The arkId to search for
     * @return An optional containing the concept if found
     */
    @Query(
            nativeQuery = true,
            value = "SELECT c.* FROM concept c WHERE c.fk_ark_id = :arkId"
    )
    Optional<Concept> findConceptByArkId(@Param("arkId") String arkId);

    /**
     * Find a concept by its external ids.
     * @param idt The ID of the external vocabulary
     * @param idc The ID of the concept in the external vocabulary
     * @return An optional containing the concept if found
     */
    @Query(
            "SELECT c FROM Concept c " +
                    "JOIN c.vocabulary v " +
                    "WHERE LOWER(v.externalVocabularyId) = LOWER(:idt) AND LOWER(c.externalId) = LOWER(:idc)"
    )
    Optional<Concept> findConceptByExternalIdIgnoreCase(String idt, String idc);

    /**
     * Find the top term configuration for a field code of a user.
     * @param fieldCode The code of the field
     * @return An optional containing the concept if found
     */
    @Query(
            nativeQuery = true,
            value = "SELECT c.* FROM concept c " +
                    "JOIN concept_field_config cfc ON cfc.fk_concept_id = c.concept_id " +
                    "WHERE cfc.fk_institution_id = :institutionId AND " +
                    "cfc.fk_user_id = :userId AND " +
                    "cfc.field_code = :fieldCode"
    )
    Optional<Concept> findTopTermConfigForFieldCodeOfUser(Long institutionId, Long userId, String fieldCode);

    @Query(
            nativeQuery = true,
            value = "SELECT c.* FROM concept c " +
                    "JOIN concept_field_config cfc ON cfc.fk_concept_id = c.concept_id " +
                    "WHERE cfc.fk_institution_id = :institutionId AND cfc.field_code = :fieldCode "
    )
    Optional<Concept> findTopTermConfigForFieldCodeOfInstitution(Long institutionId, String fieldCode);
}
