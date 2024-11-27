package fr.siamois.infrastructure.repositories;

import fr.siamois.models.Field;
import fr.siamois.models.auth.Person;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FieldRepository extends CrudRepository<Field, Long> {

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO field_vocabulary_collection(fk_collection_id, fk_field_id) " +
                    "VALUES (:collectionId,:fieldId)"
    )
    int saveCollectionWithField(@Param("collectionId") Long collectionId, @Param("fieldId") Long fieldId);

    Optional<Field> findByUserAndFieldCode(Person user, @NotNull String fieldCode);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "DELETE FROM field_vocabulary_collection fvc " +
                    "WHERE fk_field_id IN " +
                    "( SELECT f.field_id " +
                    "FROM field f " +
                    "WHERE fk_user_id = :personId " +
                    "AND field_code = :fieldCode )"
    )
    int deleteVocabularyCollectionConfigurationByPersonAndFieldCode(Long personId, String fieldCode);

    @Transactional
    @Modifying
    @Query(
            nativeQuery = true,
            value = "UPDATE field " +
                    "SET fk_vocabulary_id = NULL " +
                    "WHERE field_code = :fieldCode AND fk_user_id = :personId"
    )
    int deleteVocabularyConfigurationByPersonAndFieldCode(Long personId, String fieldCode);

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
