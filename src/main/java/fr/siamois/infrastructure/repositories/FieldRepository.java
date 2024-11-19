package fr.siamois.infrastructure.repositories;

import fr.siamois.models.Field;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FieldRepository extends CrudRepository<Field, Long> {

    @Modifying
    @Query(
            nativeQuery = true,
            value = "INSERT INTO field_vocabulary_collection(fk_collection_id, fk_field_id) " +
                    "VALUES (:collectionId,:fieldId)"
    )
    boolean saveFieldWithCollection(@Param("collectionId") Long collectionId, @Param("fieldId") Long fieldId);

}
