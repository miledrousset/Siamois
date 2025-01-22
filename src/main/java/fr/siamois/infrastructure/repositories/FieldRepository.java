package fr.siamois.infrastructure.repositories;

import fr.siamois.models.Field;
import fr.siamois.models.auth.Person;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FieldRepository extends CrudRepository<Field, Long> {

    /**
     * Set a collection configuration parameter to a field.
     * @param collectionId The id of the collection
     * @param fieldId The id of the field
     * @return The number of rows affected
     */
    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO field_vocabulary_collection(fk_collection_id, fk_field_id) " +
                    "VALUES (:collectionId,:fieldId)"
    )
    int saveCollectionWithField(@Param("collectionId") Long collectionId, @Param("fieldId") Long fieldId);

    /**
     * Find a field by its user and field code.
     * @param user The user
     * @param fieldCode The code of the field
     * @return An optional containing the field if found
     */
    Optional<Field> findByUserAndFieldCode(Person user, @NotNull String fieldCode);

    /**
     * Deletes all the vocabulary collection configuration of a person by a field code.
     * @param personId The id of the person
     * @param fieldCode The code of the field
     * @return The number of rows affected
     */
    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "DELETE FROM field_vocabulary_collection fvc " +
                    "WHERE fvc.fk_field_id IN ( SELECT f.field_id FROM field f WHERE f.fk_user_id = :personId AND f.field_code = :fieldCode)"
    )
    int deleteVocabularyCollectionConfigurationByPersonAndFieldCode(Long personId, String fieldCode);

    /**
     * Deletes the vocabulary configuration of a person by a field code.
     * @param personId The id of the person
     * @param fieldCode The code of the field
     * @return The number of rows affected
     */
    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "UPDATE field " +
                    "SET fk_vocabulary_id = NULL " +
                    "WHERE field_code = :fieldCode AND fk_user_id = :personId"
    )
    int deleteVocabularyConfigurationByPersonAndFieldCode(Long personId, String fieldCode);

    /**
     * Saves a vocabulary configuration to a field.
     * @param fieldId The id of the field
     * @param vocabId The id of the vocabulary
     * @return The number of rows affected
     */
    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "UPDATE field " +
                    "SET fk_vocabulary_id = :vocabId " +
                    "WHERE field_id = :fieldId"
    )
    int saveVocabularyWithField(Long fieldId, Long vocabId);
}
