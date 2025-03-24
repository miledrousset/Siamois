package fr.siamois.infrastructure.database.initializer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.siamois.domain.models.exceptions.database.DatabaseDataInitException;
import fr.siamois.domain.models.vocabulary.VocabularyType;
import fr.siamois.infrastructure.database.repositories.vocabulary.VocabularyTypeRepository;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.repository.CrudRepository;

class VocabularyTypeInitializerDiffblueTest {
    /**
     * Test {@link VocabularyTypeInitializer#initialize()}.
     * <p>
     * Method under test: {@link VocabularyTypeInitializer#initialize()}
     */
    @Test
    @DisplayName("Test initialize()")
    @Tag("MaintainedByDiffblue")
    void testInitialize() throws DatabaseDataInitException {
        // Arrange
        VocabularyType vocabularyType = new VocabularyType();
        vocabularyType.setId(1L);
        vocabularyType.setLabel("Label");
        Optional<VocabularyType> ofResult = Optional.of(vocabularyType);
        VocabularyTypeRepository vocabularyTypeRepository = mock(VocabularyTypeRepository.class);
        when(vocabularyTypeRepository.findVocabularyTypeByLabel(anyString())).thenReturn(ofResult);

        // Act
        (new VocabularyTypeInitializer(vocabularyTypeRepository)).initialize();

        // Assert
        verify(vocabularyTypeRepository, atLeast(1)).findVocabularyTypeByLabel(anyString());
    }

    /**
     * Test {@link VocabularyTypeInitializer#initialize()}.
     * <ul>
     *   <li>Then calls {@link CrudRepository#save(Object)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link VocabularyTypeInitializer#initialize()}
     */
    @Test
    @DisplayName("Test initialize(); then calls save(Object)")
    @Tag("MaintainedByDiffblue")
    void testInitialize_thenCallsSave() throws DatabaseDataInitException {
        // Arrange
        VocabularyType vocabularyType = new VocabularyType();
        vocabularyType.setId(1L);
        vocabularyType.setLabel("Label");
        VocabularyTypeRepository vocabularyTypeRepository = mock(VocabularyTypeRepository.class);
        when(vocabularyTypeRepository.save(Mockito.<VocabularyType>any())).thenReturn(vocabularyType);
        Optional<VocabularyType> emptyResult = Optional.empty();
        when(vocabularyTypeRepository.findVocabularyTypeByLabel(anyString())).thenReturn(emptyResult);

        // Act
        (new VocabularyTypeInitializer(vocabularyTypeRepository)).initialize();

        // Assert
        verify(vocabularyTypeRepository, atLeast(1)).findVocabularyTypeByLabel(anyString());
        verify(vocabularyTypeRepository, atLeast(1)).save(Mockito.<VocabularyType>any());
    }
}
