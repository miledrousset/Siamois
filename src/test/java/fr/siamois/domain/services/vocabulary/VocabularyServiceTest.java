package fr.siamois.domain.services.vocabulary;

import fr.siamois.domain.services.vocabulary.VocabularyService;
import fr.siamois.infrastructure.api.ThesaurusApi;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyRepository;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyTypeRepository;
import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.models.vocabulary.VocabularyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VocabularyServiceTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private ThesaurusApi thesaurusApi;

    @Mock
    private VocabularyTypeRepository vocabularyTypeRepository;

    @InjectMocks
    private VocabularyService vocabularyService;

    private Vocabulary vocabulary;

    @BeforeEach
    void setUp() {
        vocabulary = new Vocabulary();
        vocabulary.setId(1L);
    }

    @Test
    void findById_Success() {
        when(vocabularyRepository.findById(1L)).thenReturn(Optional.ofNullable(vocabulary));

        Vocabulary actualResult = vocabularyService.findVocabularyById(1L);

        assertEquals(vocabulary, actualResult);
    }

    @Test
    void findAllPublicThesaurus_Success() throws InvalidEndpointException {
        String vocabInstanceUri = "http://example.com";
        String languageCode = "en";
        List<ThesaurusDTO> thesaurusDTOList = new ArrayList<>();
        ThesaurusDTO thesaurusDTO = new ThesaurusDTO();
        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setLang("en");
        labelDTO.setTitle("Test Thesaurus");
        thesaurusDTO.setLabels(List.of(labelDTO));
        thesaurusDTO.setIdTheso("123");
        thesaurusDTOList.add(thesaurusDTO);

        when(thesaurusApi.fetchAllPublicThesaurus(vocabInstanceUri)).thenReturn(thesaurusDTOList);
        when(vocabularyTypeRepository.findVocabularyTypeByLabel("Thesaurus")).thenReturn(Optional.of(new VocabularyType()));

        List<Vocabulary> result = vocabularyService.findAllPublicThesaurus(vocabInstanceUri, languageCode);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Thesaurus", result.get(0).getVocabularyName());
    }

    @Test
    void saveOrGetVocabulary_Success() {
        Vocabulary newVocabulary = new Vocabulary();
        newVocabulary.setBaseUri("http://example.com");
        newVocabulary.setExternalVocabularyId("123");

        when(vocabularyRepository.findVocabularyByBaseUriAndVocabExternalId(newVocabulary.getBaseUri(), newVocabulary.getExternalVocabularyId())).thenReturn(Optional.empty());
        when(vocabularyRepository.save(newVocabulary)).thenReturn(newVocabulary);

        Vocabulary result = vocabularyService.saveOrGetVocabulary(newVocabulary);

        assertNotNull(result);
        assertEquals(newVocabulary, result);
    }
}