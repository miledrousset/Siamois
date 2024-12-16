package fr.siamois.services.vocabulary;

import fr.siamois.infrastructure.repositories.vocabulary.VocabularyRepository;
import fr.siamois.models.exceptions.VocabularyNotFoundException;
import fr.siamois.models.vocabulary.Vocabulary;
import org.springframework.stereotype.Service;

/**
 * Service to manage Vocabulary
 *
 * @author Julien Linget
 */
@Service
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    public VocabularyService(VocabularyRepository vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    /**
     * Find a action unit by its ID
     *
     * @param id The ID to fetch
     * @return The Vocabulary having the given ID
     * @throws VocabularyNotFoundException If no vocabulary is found for the given id
     */
    public Vocabulary findVocabularyById(long id) {
        return this.vocabularyRepository.findById(id).orElseThrow(() -> new VocabularyNotFoundException("Vocabulary not found with ID: " + id));
    }


}
