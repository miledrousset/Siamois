package fr.siamois.domain.services.vocabulary;

import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.models.vocabulary.VocabularyType;
import fr.siamois.infrastructure.api.ThesaurusApi;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import fr.siamois.infrastructure.database.repositories.vocabulary.VocabularyRepository;
import fr.siamois.infrastructure.database.repositories.vocabulary.VocabularyTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VocabularyServiceTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private ThesaurusApi thesaurusApi;

    @Mock
    private VocabularyTypeRepository vocabularyTypeRepository;

    @Mock
    private LabelService labelService;

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

    @Test
    void findOrCreateVocabularyOfUri_Success() throws InvalidEndpointException {
        String uri = "http://example.com/openapi/v1/thesaurus?idt=123";

        ThesaurusDTO thesaurusDTO = new ThesaurusDTO();
        thesaurusDTO.setIdTheso("123");
        thesaurusDTO.setBaseUri("http://example.com");
        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setLang("en");
        labelDTO.setTitle("Test Thesaurus");
        thesaurusDTO.setLabels(List.of(labelDTO));

        VocabularyType vocabularyType = new VocabularyType();
        vocabularyType.setLabel("Thesaurus");

        when(thesaurusApi.fetchThesaurusInfo(uri)).thenReturn(thesaurusDTO);
        when(vocabularyTypeRepository.findVocabularyTypeByLabel("Thesaurus")).thenReturn(Optional.of(vocabularyType));
        when(vocabularyRepository.save(any(Vocabulary.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vocabulary result = vocabularyService.findOrCreateVocabularyOfUri(uri);

        assertNotNull(result);
        assertEquals("123", result.getExternalVocabularyId());
        assertEquals("http://example.com", result.getBaseUri());
        assertEquals(vocabularyType, result.getType());
    }

}