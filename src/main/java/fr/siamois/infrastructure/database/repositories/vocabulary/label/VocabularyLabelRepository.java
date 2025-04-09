package fr.siamois.infrastructure.database.repositories.vocabulary.label;

import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.models.vocabulary.label.VocabularyLabel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyLabelRepository extends CrudRepository<VocabularyLabel, Long> {
    Optional<VocabularyLabel> findByVocabularyAndLangCode(Vocabulary vocabulary, String langCode);

    List<VocabularyLabel> findAllByVocabulary(Vocabulary vocabulary);
}
