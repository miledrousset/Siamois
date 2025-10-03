package fr.siamois.infrastructure.database.initializer.seeder;


import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.services.vocabulary.FieldConfigurationService;
import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.infrastructure.database.repositories.vocabulary.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ThesaurusSeeder {
    private final VocabularyRepository vocabularyRepository;
    private final VocabularyService vocabularyService;
    private final FieldConfigurationService fieldConfigurationService;

    public record ThesaurusSpec(String baseUri, String externalId) {
    }

    public Map<String, Vocabulary> seed(List<ThesaurusSpec> specs) throws DatabaseDataInitException {
        Map<String, Vocabulary> result = new HashMap<>();
        for (var s : specs) {
            String baseUri = s.baseUri();
            String externalId = s.externalId();

            Vocabulary vocab = vocabularyRepository
                    .findVocabularyByBaseUriAndVocabExternalId(baseUri, externalId)
                    .orElse(null);


            if(vocab == null) {
                String fullUri = baseUri + "?idt=" + externalId;
                try {
                    result.put(s.externalId(), vocabularyService.findOrCreateVocabularyOfUri(fullUri));
                } catch (InvalidEndpointException e) {
                    throw new DatabaseDataInitException("Error creating vocabulary from URI: " + fullUri, e);
                }
            }
            else {
                result.put(s.externalId(), vocab);
            }

        }
        return result;
    }
}
