package fr.siamois.infrastructure.repositories;

import fr.siamois.domain.models.Field;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends CrudRepository<Field, Long> {

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO concept_field_config(fk_institution_id, field_code, fk_concept_id) " +
                    "VALUES (:institutionId, :fieldCode, :conceptId)"
    )
    void saveConceptForFieldOfInstitution(Long institutionId, String fieldCode, Long conceptId);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO concept_field_config(fk_institution_id, fk_user_id, fk_concept_id, field_code) " +
                    "VALUES (:institutionId, :userId, :conceptId, :fieldCode)"
    )
    void saveConceptForFieldOfUser(Long institutionId, Long userId, String fieldCode, Long conceptId);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "UPDATE concept_field_config " +
                    "SET fk_concept_id = :conceptId " +
                    "WHERE fk_institution_id = :institutionId AND field_code = :fieldCode AND fk_user_id IS NULL"
    )
    int updateConfigForFieldOfInstitution(Long institutionId, String fieldCode, Long conceptId);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "UPDATE concept_field_config " +
                    "SET fk_concept_id = :conceptId " +
                    "WHERE fk_institution_id = :institutionId AND field_code = :fieldCode AND fk_user_id = :userId"
    )
    int updateConfigForFieldOfUser(Long institutionId, Long userId, String fieldCode, Long conceptId);
}
