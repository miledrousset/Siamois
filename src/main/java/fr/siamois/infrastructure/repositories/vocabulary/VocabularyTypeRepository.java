package fr.siamois.infrastructure.repositories.vocabulary;

import fr.siamois.models.vocabulary.VocabularyType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VocabularyTypeRepository extends CrudRepository<VocabularyType, Long> {

    /**
     * Find a vocabulary type by its label.
     * @param label The label of the vocabulary type
     * @return An optional containing the vocabulary type if found
     */
    Optional<VocabularyType> findVocabularyTypeByLabel(String label);

}

