package fr.siamois.infrastructure.repositories;

import fr.siamois.models.Field;
import fr.siamois.models.Person;
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
            value = "UPDATE field_vocabulary_collection " +
                    "SET fk_collection_id = :collectionId " +
                    "WHERE fk_field_id = :fieldId"
    )
    int changeCollectionOfField(@Param("collectionId") Long collectionId, @Param("fieldId") Long fieldId);
}
