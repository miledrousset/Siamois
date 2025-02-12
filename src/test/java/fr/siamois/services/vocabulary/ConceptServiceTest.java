package fr.siamois.services.vocabulary;

import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.infrastructure.api.dto.PurlInfoDTO;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import fr.siamois.models.Institution;
import fr.siamois.models.UserInfo;
import fr.siamois.models.auth.Person;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConceptServiceTest {

    @Mock
    private ConceptRepository repository;

    @InjectMocks
    private ConceptService conceptService;

    private Vocabulary vocabulary;

    @BeforeEach
    void beforeEach() {
        VocabularyType type = new VocabularyType();
        type.setId(-1L);
        type.setLabel("Thesaurus");

        vocabulary = new Vocabulary();
        vocabulary.setId(-1L);
        vocabulary.setBaseUri("http://localhost:8080");
        vocabulary.setExternalVocabularyId("th233");
        vocabulary.setVocabularyName("Test thesaurus");
        vocabulary.setType(type);

    }

    @Test
    void saveOrGetConcept_shoudSaveConcept_whenNotExist() {
        when(repository.findConceptByExternalIdIgnoreCase("th233", "1003")).thenReturn(Optional.empty());
        when(repository.save(any(Concept.class))).then(invocation -> invocation.getArgument(0, Concept.class));

        Concept concept = new Concept();
        concept.setVocabulary(vocabulary);
        concept.setLangCode("fr");
        concept.setExternalId("1003");
        concept.setLabel("Test Concept");

        Concept result = conceptService.saveOrGetConcept(concept);

        assertThat(result).isEqualTo(concept);
        verify(repository).save(any(Concept.class));
    }

    @Test
    void saveOrGetConcept_shoudGetConcept_whenExist() {
        Concept refConcept = new Concept();
        refConcept.setVocabulary(vocabulary);
        refConcept.setLangCode("fr");
        refConcept.setExternalId("1003");
        refConcept.setLabel("Test Concept");
        refConcept.setId(-120L);

        when(repository.findConceptByExternalIdIgnoreCase("th233", "1003")).thenReturn(Optional.of(refConcept));

        Concept concept = new Concept();
        concept.setVocabulary(vocabulary);
        concept.setLangCode("fr");
        concept.setExternalId("1003");
        concept.setLabel("Test Concept");

        Concept result = conceptService.saveOrGetConcept(concept);

        assertThat(result).isEqualTo(refConcept);
        verify(repository, never()).save(any(Concept.class));
    }

    private FullConceptDTO createDto() {
        PurlInfoDTO id = new PurlInfoDTO();
        id.setType("string");
        id.setValue("1023");

        PurlInfoDTO prefLabel = new PurlInfoDTO();
        prefLabel.setLang("fr");
        prefLabel.setValue("Test label");
        prefLabel.setType("string");

        FullConceptDTO dto = new FullConceptDTO();
        dto.setIdentifier(new PurlInfoDTO[]{ id });
        dto.setPrefLabel(new PurlInfoDTO[]{ prefLabel });

        return dto;
    }

    @Test
    void saveOrGetConceptFromFullDTO_shouldSaveNewConcept_whenNotExist() {
        FullConceptDTO dto = createDto();

        when(repository.findConceptByExternalIdIgnoreCase(vocabulary.getExternalVocabularyId(), "1023"))
                .thenReturn(Optional.empty());
        when(repository.save(any(Concept.class))).then(invocation -> invocation.getArgument(0, Concept.class));

        Concept refConcept = new Concept();
        refConcept.setLabel("Test label");
        refConcept.setLangCode("fr");
        refConcept.setVocabulary(vocabulary);
        refConcept.setExternalId("1023");

        UserInfo info = new UserInfo(new Institution(), new Person(), "fr");

        Concept result = conceptService.saveOrGetConceptFromFullDTO(info, vocabulary, dto);

        assertThat(result).isEqualTo(refConcept);
        verify(repository).save(any(Concept.class));
    }

    @Test
    void saveOrGetConceptFromFullDTO_shouldReturnConcept_whenExist() {
        FullConceptDTO dto = createDto();

        Concept refConcept = new Concept();
        refConcept.setLabel("Test label");
        refConcept.setLangCode("fr");
        refConcept.setVocabulary(vocabulary);
        refConcept.setExternalId("1023");
        refConcept.setId(-12L);

        when(repository.findConceptByExternalIdIgnoreCase(vocabulary.getExternalVocabularyId(), "1023"))
                .thenReturn(Optional.of(refConcept));

        UserInfo info = new UserInfo(new Institution(), new Person(), "fr");

        Concept result = conceptService.saveOrGetConceptFromFullDTO(info, vocabulary, dto);

        assertThat(result).isEqualTo(refConcept);
        verify(repository, never()).save(any(Concept.class));
    }

}