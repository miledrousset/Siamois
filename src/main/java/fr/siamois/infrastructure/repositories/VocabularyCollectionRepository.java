package fr.siamois.infrastructure.repositories;

import fr.siamois.models.Vocabulary;
import fr.siamois.models.VocabularyCollection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VocabularyCollectionRepository extends CrudRepository<VocabularyCollection, Long> {
    Optional<VocabularyCollection> findByVocabularyAndExternalId(Vocabulary vocabulary, String idGroup);

    @Query(
            nativeQuery = true,
            value = "SELECT c.* FROM vocabulary_collection c " +
                    "JOIN field_vocabulary_collection fvc ON fvc.fk_collection_id = c.vocabulary_collection_id " +
                    "JOIN field f ON fvc.fk_field_id = f.field_id " +
                    "WHERE f.fk_user_id = :personId AND f.field_code = :fieldCode"
    )
    Optional<VocabularyCollection> findVocabularyCollectionByPersonAndFieldCode(@Param("personId") Long personId,
                                                                            @Param("fieldCode") String fieldCode);
}
