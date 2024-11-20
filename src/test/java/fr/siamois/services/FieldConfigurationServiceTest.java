package fr.siamois.services;

import fr.siamois.infrastructure.api.ThesaurusApi;
import fr.siamois.infrastructure.api.ThesaurusCollectionApi;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import fr.siamois.infrastructure.api.dto.VocabularyCollectionDTO;
import fr.siamois.infrastructure.repositories.FieldRepository;
import fr.siamois.infrastructure.repositories.VocabularyCollectionRepository;
import fr.siamois.infrastructure.repositories.VocabularyRepository;
import fr.siamois.infrastructure.repositories.VocabularyTypeRepository;
import fr.siamois.models.*;
import fr.siamois.models.exceptions.field.FailedFieldSaveException;
import fr.siamois.models.exceptions.field.FailedFieldUpdateException;
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
    private VocabularyType thesaurusType;
    private Vocabulary th21;
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

        thesaurusType = new VocabularyType();
        thesaurusType.setId(1L);
        thesaurusType.setLabel("Thesaurus");

        when(vocabularyTypeRepository.findVocabularyTypeByLabel("Thesaurus")).thenReturn(Optional.of(thesaurusType));

        th21 = new Vocabulary();
        th21.setExternalVocabularyId("th21");
        th21.setId(1L);
        th21.setVocabularyName("Test name");
        th21.setType(thesaurusType);
        th21.setBaseUri("http://example.com");

        when(vocabularyRepository
                .findVocabularyByBaseUriIgnoreCaseAndExternalVocabularyIdIgnoreCase("http://example.com", "th21"))
                .thenReturn(Optional.of(th21));

        g21 = new VocabularyCollection();
        g21.setId(1L);
        g21.setVocabulary(th21);
        g21.setExternalId("g21");

        when(vocabularyCollectionRepository.findByVocabularyAndExternalId(th21, "g21"))
                .thenReturn(Optional.of(g21));

        ThesaurusDTO thesaurusDTO = new ThesaurusDTO();
        thesaurusDTO.setIdTheso("th21");
        thesaurusDTO.setLabels(new ArrayList<>());

        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setLang("fr");
        labelDTO.setTitle("Test name");

        thesaurusDTO.getLabels().add(labelDTO);

        when(thesaurusApi.fetchThesaurusInfos("http://example.com", "th21"))
                .thenReturn(Optional.of(thesaurusDTO));

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

        assertThrows(FailedFieldUpdateException.class, () -> {
            service.saveThesaurusFieldConfiguration(person,
                    SpatialUnit.CATEGORY_FIELD_CODE,
                    "http://example.com",
                    "th21",
                    dto
            );
        });
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

        assertThrows(FailedFieldSaveException.class, () -> {
            service.saveThesaurusFieldConfiguration(person,
                    SpatialUnit.CATEGORY_FIELD_CODE,
                    "http://example.com",
                    "th21",
                    dto
            );
        });

    }
}