package fr.siamois.infrastructure.repositories.vocabulary;

import fr.siamois.models.vocabulary.VocabularyType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VocabularyTypeRepository extends CrudRepository<VocabularyType, Long> {

    Optional<VocabularyType> findVocabularyTypeByLabel(String label);

}

