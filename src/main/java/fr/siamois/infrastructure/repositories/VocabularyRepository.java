package fr.siamois.infrastructure.repositories;

import fr.siamois.models.Vocabulary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VocabularyRepository extends CrudRepository<Vocabulary, Long> {

    Optional<Vocabulary> findVocabularyByBaseUriIgnoreCaseAndExternalVocabularyIdIgnoreCase(String baseUri, String externalId);

}

