package fr.siamois.services;

import fr.siamois.infrastructure.api.ThesaurusApi;
import fr.siamois.infrastructure.api.ThesaurusCollectionApi;
import fr.siamois.infrastructure.repositories.FieldRepository;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyCollectionRepository;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyRepository;
import fr.siamois.infrastructure.repositories.vocabulary.VocabularyTypeRepository;
import fr.siamois.models.Field;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.auth.Person;
import fr.siamois.models.exceptions.field.FailedFieldSaveException;
import fr.siamois.models.exceptions.field.FailedFieldUpdateException;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FieldConfigurationServiceTest {

    @Mock
    private VocabularyTypeRepository vocabularyTypeRepository;
    @Mock
    private ThesaurusApi thesaurusApi;
    @Mock
    private VocabularyRepository vocabularyRepository;
    @Mock
    private VocabularyCollectionRepository vocabularyCollectionRepository;
    @Mock
    private FieldRepository fieldRepository;
    @Mock
    private ThesaurusCollectionApi thesaurusCollectionApi;

    private FieldConfigurationService service;

    private Person person;

    private Vocabulary vocabulary;
    private VocabularyCollection vocabularyCollection;
    private Field field;

    @BeforeEach
    public void setUp() {
        service = new FieldConfigurationService(vocabularyTypeRepository,
                thesaurusApi,
                vocabularyRepository,
                vocabularyCollectionRepository,
                fieldRepository,
                thesaurusCollectionApi);

        person = new Person();
        person.setId(1L);
        person.setUsername("test");
        person.setPassword("unhashedPassword");

        vocabulary = new Vocabulary();
        vocabulary.setId(-1L);
        vocabulary.setExternalVocabularyId("th21");

        vocabularyCollection = new VocabularyCollection();
        vocabularyCollection.setId(-1L);
        vocabularyCollection.setVocabulary(vocabulary);

        field = new Field();
        field.setId(-1L);
        field.setUser(person);
        field.setFieldCode(SpatialUnit.CATEGORY_FIELD_CODE);

    }

    @Test
    public void saveThesaurusCollectionFieldConfiguraiton_shouldCreateField_whenNoFieldExistAndNoConfigurationExist() throws FailedFieldSaveException, FailedFieldUpdateException {
        // GIVEN
        when(vocabularyCollectionRepository.findAllVocabularyCollectionByPersonAndFieldCode(person.getId(), field.getFieldCode()))
                .thenReturn(List.of());

        when(vocabularyRepository.findVocabularyOfUserForField(person.getId(), field.getFieldCode()))
                .thenReturn(Optional.empty());

        when(fieldRepository.save(any(Field.class))).thenReturn(field);

        when(fieldRepository.saveCollectionWithField(vocabularyCollection.getId(), field.getId())).thenReturn(1);

        // WHEN
        service.saveThesaurusCollectionFieldConfiguration(person, field.getFieldCode(), List.of(vocabularyCollection));

        // THEN
        verify(fieldRepository, times(1)).save(any(Field.class));
        verify(fieldRepository, times(1)).saveCollectionWithField(vocabulary.getId(), field.getId());
    }

    @Test
    public void saveThesaurusCollectionFieldConfiguration_shouldUpdateField_whenNewCollectionIsGivenAndPreviousWasCollection() throws FailedFieldSaveException, FailedFieldUpdateException {
        // GIVEN
        VocabularyCollection newCollection = new VocabularyCollection();
        newCollection.setId(-2L);
        newCollection.setVocabulary(vocabulary);
        newCollection.setExternalId("g2132");

        when(vocabularyCollectionRepository.findAllVocabularyCollectionByPersonAndFieldCode(person.getId(), field.getFieldCode()))
                .thenReturn(List.of(vocabularyCollection));

        when(fieldRepository.deleteVocabularyCollectionConfigurationByPersonAndFieldCode(person.getId(), field.getFieldCode()))
                .thenReturn(1);

        when(vocabularyRepository.findVocabularyOfUserForField(person.getId(), field.getFieldCode()))
                .thenReturn(Optional.empty());

        when(fieldRepository.findByUserAndFieldCode(person, field.getFieldCode())).thenReturn(Optional.of(field));

        when(fieldRepository.saveCollectionWithField(newCollection.getId(), field.getId()))
                .thenReturn(1);

        // WHEN
        service.saveThesaurusCollectionFieldConfiguration(person, field.getFieldCode(), List.of(newCollection));

        // THEN
        verify(fieldRepository, times(1)).deleteVocabularyCollectionConfigurationByPersonAndFieldCode(person.getId(), field.getFieldCode());
        verify(fieldRepository, times(1)).saveCollectionWithField(newCollection.getId(), field.getId());
    }

    @Test
    public void saveThesaurusCollectionFieldConfiguration_shouldUpdateField_whenNewCollectionIsGivenAndPreviousWasVocab() throws FailedFieldSaveException, FailedFieldUpdateException {
        // GIVEN
        VocabularyCollection newCollection = new VocabularyCollection();
        newCollection.setId(-2L);
        newCollection.setVocabulary(vocabulary);
        newCollection.setExternalId("g2132");

        when(vocabularyCollectionRepository.findAllVocabularyCollectionByPersonAndFieldCode(person.getId(), field.getFieldCode()))
                .thenReturn(List.of());

        when(vocabularyRepository.findVocabularyOfUserForField(person.getId(), field.getFieldCode()))
                .thenReturn(Optional.of(vocabulary));

        when(fieldRepository.deleteVocabularyConfigurationByPersonAndFieldCode(person.getId(), field.getFieldCode()))
                .thenReturn(1);

        when(fieldRepository.findByUserAndFieldCode(person, field.getFieldCode())).thenReturn(Optional.of(field));

        when(fieldRepository.saveCollectionWithField(newCollection.getId(), field.getId()))
                .thenReturn(1);

        // WHEN
        service.saveThesaurusCollectionFieldConfiguration(person, field.getFieldCode(), List.of(newCollection));

        // THEN
        verify(fieldRepository, times(1)).deleteVocabularyConfigurationByPersonAndFieldCode(person.getId(), field.getFieldCode());
        verify(fieldRepository, times(1)).saveCollectionWithField(newCollection.getId(), field.getId());
    }

    @Test
    public void saveThesaurusCollectionFieldConfiguration_shouldThrow_whenNewCollectionIsTheSame() {
        // GIVEN
        when(vocabularyCollectionRepository.findAllVocabularyCollectionByPersonAndFieldCode(person.getId(), field.getFieldCode()))
                .thenReturn(List.of(vocabularyCollection));

        // WHEN
        assertThrows(FailedFieldUpdateException.class, () -> {
            service.saveThesaurusCollectionFieldConfiguration(person, field.getFieldCode(), List.of(vocabularyCollection));
        });
    }

    @Test
    public void saveThesaurusFieldConfiguration_shouldCreateField_whenNoFieldExistAndNoConfigurationExist() throws FailedFieldSaveException, FailedFieldUpdateException {
        // GIVEN
        when(vocabularyRepository.findVocabularyOfUserForField(person.getId(), field.getFieldCode()))
                .thenReturn(Optional.empty());

        when(fieldRepository.save(any(Field.class))).thenReturn(field);

        when(fieldRepository.saveVocabularyWithField(vocabulary.getId(), field.getId())).thenReturn(1);

        // WHEN
        service.saveThesaurusFieldConfiguration(person, field.getFieldCode(), vocabulary);

        // THEN
        verify(fieldRepository, times(1)).save(any(Field.class));
        verify(fieldRepository, times(1)).saveVocabularyWithField(field.getId(), vocabulary.getId());
    }

    @Test
    public void saveThesaurusCollectionFieldConfiguration_shouldThrow_whenNewVocabIsTheSame() {
        when(vocabularyRepository.findVocabularyOfUserForField(person.getId(), field.getFieldCode()))
                .thenReturn(Optional.of(vocabulary));

        assertThrows(FailedFieldUpdateException.class, () -> {
            service.saveThesaurusFieldConfiguration(person, field.getFieldCode(), vocabulary);
        });
    }

}