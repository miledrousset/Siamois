package fr.siamois.infrastructure.repositories;

import fr.siamois.models.Field;
import fr.siamois.models.auth.Person;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FieldRepository extends CrudRepository<Field, Long> {

    /**
     * Find a field by its user and field code.
     * @param user The user
     * @param fieldCode The code of the field
     * @return An optional containing the field if found
     */
    Optional<Field> findByUserAndFieldCode(Person user, @NotNull String fieldCode);

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
            value = "DELETE FROM concept_field_config cfc " +
                    "WHERE cfc.fk_institution_id = :institutionId AND " +
                    "cfc.fk_user_id = :personId AND " +
                    "cfc.field_code = :fieldCode"
    )
    void deleteConfigurationOfUser(Long institutionId, Long personId, String fieldCode);
}
