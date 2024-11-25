package fr.siamois.services;

import fr.siamois.infrastructure.api.ThesaurusApi;
import fr.siamois.infrastructure.api.ThesaurusCollectionApi;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import fr.siamois.infrastructure.api.dto.VocabularyCollectionDTO;
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
import fr.siamois.models.vocabulary.VocabularyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    private VocabularyCollection g21;

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

        VocabularyType thesaurusType = new VocabularyType();
        thesaurusType.setId(1L);
        thesaurusType.setLabel("Thesaurus");

        Vocabulary th21 = new Vocabulary();
        th21.setExternalVocabularyId("th21");
        th21.setId(1L);
        th21.setVocabularyName("Test name");
        th21.setType(thesaurusType);
        th21.setBaseUri("http://example.com");

        g21 = new VocabularyCollection();
        g21.setId(1L);
        g21.setVocabulary(th21);
        g21.setExternalId("g21");

        ThesaurusDTO thesaurusDTO = new ThesaurusDTO();
        thesaurusDTO.setIdTheso("th21");
        thesaurusDTO.setLabels(new ArrayList<>());

        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setLang("fr");
        labelDTO.setTitle("Test name");

        thesaurusDTO.getLabels().add(labelDTO);

    }

    @Test
    void saveThesaurusFieldConfiguration_shouldThrow_whenNoUpdate() {
        // GIVEN
        Field field = new Field();
        field.setFieldCode(SpatialUnit.CATEGORY_FIELD_CODE);
        field.setUser(person);
        field.setId(1L);

        VocabularyCollectionDTO dto = new VocabularyCollectionDTO();
        dto.setIdGroup("g21");
        dto.setLabels(new ArrayList<>());

        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setLang("fr");
        labelDTO.setTitle("Groupe test");

        dto.getLabels().add(labelDTO);

        when(fieldRepository.findByUserAndFieldCode(person, SpatialUnit.CATEGORY_FIELD_CODE))
                .thenReturn(Optional.of(field));

        when(vocabularyCollectionRepository.findVocabularyCollectionByPersonAndFieldCode(person.getId(), SpatialUnit.CATEGORY_FIELD_CODE))
                .thenReturn(Optional.of(g21));

        assertThrows(FailedFieldUpdateException.class, () ->
                service.saveThesaurusFieldConfiguration(person,
                SpatialUnit.CATEGORY_FIELD_CODE,
                        g21
        ));
    }

    @Test
    void saveThesaurusFieldConfiguration_shouldThrow_whenSaveError() {
        // GIVEN
        Field field = new Field();
        field.setFieldCode(SpatialUnit.CATEGORY_FIELD_CODE);
        field.setUser(person);
        field.setId(1L);

        VocabularyCollectionDTO dto = new VocabularyCollectionDTO();
        dto.setIdGroup("g21");
        dto.setLabels(new ArrayList<>());

        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setLang("fr");
        labelDTO.setTitle("Groupe test");

        dto.getLabels().add(labelDTO);

        when(vocabularyCollectionRepository.findVocabularyCollectionByPersonAndFieldCode(person.getId(), SpatialUnit.CATEGORY_FIELD_CODE))
                .thenReturn(Optional.empty());

        when(fieldRepository.findByUserAndFieldCode(person, SpatialUnit.CATEGORY_FIELD_CODE))
                .thenReturn(Optional.empty());

        when(fieldRepository.save(any(Field.class))).thenReturn(field);

        when(fieldRepository.saveCollectionWithField(g21.getId(), field.getId())).thenReturn(0);

        assertThrows(FailedFieldSaveException.class, () ->
                service.saveThesaurusFieldConfiguration(person,
                SpatialUnit.CATEGORY_FIELD_CODE,
                        g21
        ));

    }
}