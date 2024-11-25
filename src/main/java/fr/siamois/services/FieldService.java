package fr.siamois.services;

import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyCollectionRepository;
import fr.siamois.models.vocabulary.VocabularyCollection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to handle the fields in the application.
 * @author Julien Linget
 */
@Service
public class FieldService {

    private final VocabularyCollectionRepository vocabularyCollectionRepository;
    private final ConceptApi conceptApi;

    public FieldService(VocabularyCollectionRepository vocabularyCollectionRepository, ConceptApi conceptApi) {
        this.vocabularyCollectionRepository = vocabularyCollectionRepository;
        this.conceptApi = conceptApi;
    }

    public List<ConceptFieldDTO> fetchAutocomplete(VocabularyCollection vocabularyCollection, String input) {
        List<ConceptFieldDTO> result = conceptApi.fetchAutocomplete(vocabularyCollection, input, "fr");
        if (result == null) return new ArrayList<>();
        return result;
    }

}
