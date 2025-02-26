package fr.siamois.domain.services.vocabulary;

import fr.siamois.infrastructure.api.ThesaurusApi;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyRepository;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyTypeRepository;
import fr.siamois.domain.models.exceptions.VocabularyNotFoundException;
import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.models.vocabulary.VocabularyType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service to manage Vocabulary
 *
 * @author Julien Linget
 */
@Service
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final ThesaurusApi thesaurusApi;
    private final VocabularyTypeRepository vocabularyTypeRepository;

    public VocabularyService(VocabularyRepository vocabularyRepository, ThesaurusApi thesaurusApi, VocabularyTypeRepository vocabularyTypeRepository) {
        this.vocabularyRepository = vocabularyRepository;
        this.thesaurusApi = thesaurusApi;
        this.vocabularyTypeRepository = vocabularyTypeRepository;
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

    public List<Vocabulary> findAllPublicThesaurus(String vocabInstanceUri, String languageCode) throws InvalidEndpointException {
        List<ThesaurusDTO> thesaurusDTOList = thesaurusApi.fetchAllPublicThesaurus(vocabInstanceUri);
        List<Vocabulary> result = new ArrayList<>();

        VocabularyType type = vocabularyTypeRepository.findVocabularyTypeByLabel("Thesaurus").orElseThrow(() -> new IllegalArgumentException("Thesaurus entry does not exist"));


        for (ThesaurusDTO thesaurusDTO : thesaurusDTOList) {
            Vocabulary vocabulary = new Vocabulary();
            Optional<LabelDTO> labelForLang = thesaurusDTO.getLabels().stream()
                    .filter((labelDTO -> labelDTO.getLang().equalsIgnoreCase(languageCode)))
                    .findFirst();
            LabelDTO labelDTO = labelForLang.orElseGet(() -> thesaurusDTO.getLabels().get(0));
            vocabulary.setVocabularyName(labelDTO.getTitle());
            vocabulary.setLastLang(labelDTO.getLang());
            vocabulary.setExternalVocabularyId(thesaurusDTO.getIdTheso());
            vocabulary.setBaseUri(vocabInstanceUri);
            vocabulary.setType(type);

            result.add(vocabulary);
        }

        return result;
    }

    public Vocabulary saveOrGetVocabulary(Vocabulary vocabulary) {
        Optional<Vocabulary> vocabOpt = vocabularyRepository.findVocabularyByBaseUriAndVocabExternalId(vocabulary.getBaseUri(), vocabulary.getExternalVocabularyId());
        return vocabOpt.orElseGet(() -> vocabularyRepository.save(vocabulary));
    }
}
