package fr.siamois.infrastructure.repositories.vocabulary;

import fr.siamois.models.vocabulary.Vocabulary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VocabularyRepository extends CrudRepository<Vocabulary, Long> {

    @Query(
            value = "SELECT v FROM Vocabulary v WHERE upper(v.baseUri) = upper(:baseUri) AND upper(v.externalVocabularyId) = upper(:externalId)"
    )
    Optional<Vocabulary> findVocabularyByBaseUriAndVocabExternalId(@Param("baseUri")String baseUri, @Param("externalId") String externalId);

}

