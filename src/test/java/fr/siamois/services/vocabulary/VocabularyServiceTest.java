package fr.siamois.services.vocabulary;

import fr.siamois.infrastructure.repositories.vocabulary.VocabularyRepository;
import fr.siamois.models.vocabulary.Vocabulary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VocabularyServiceTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @InjectMocks
    private VocabularyService vocabularyService;

    Vocabulary vocabulary ;

    @BeforeEach
    void setUp() {
        vocabulary = new Vocabulary();
        vocabulary.setId(1L);
    }

    @Test
    void findById_Success() {

        when(vocabularyRepository.findById(1L)).thenReturn(Optional.ofNullable(vocabulary));

        // Act;
        Vocabulary actualResult = vocabularyService.findVocabularyById(1L);

        // Assert
        assertEquals(vocabulary, actualResult);
    }
}