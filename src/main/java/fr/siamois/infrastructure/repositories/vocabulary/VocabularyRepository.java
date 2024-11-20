package fr.siamois.infrastructure.repositories.vocabulary;

import fr.siamois.models.vocabulary.Vocabulary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VocabularyRepository extends CrudRepository<Vocabulary, Long> {

    Optional<Vocabulary> findVocabularyByBaseUriIgnoreCaseAndExternalVocabularyIdIgnoreCase(String baseUri, String externalId);

}

