package fr.siamois.domain.services.vocabulary;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.domain.models.vocabulary.VocabularyType;
import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.infrastructure.api.dto.PurlInfoDTO;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConceptServiceTest {

    @Mock
    private ConceptRepository repository;

    @Mock
    private ConceptApi conceptApi;

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

    @Test
    void findSubConceptOf_shouldReturnSubConcepts() {
        // Arrange
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setId(1L);
        vocabulary.setVocabularyName("Siamois");
        vocabulary.setExternalVocabularyId("th223");
        vocabulary.setBaseUri("https://thesaurus.mom.fr");

        Concept concept = new Concept();
        concept.setId(1L);
        concept.setVocabulary(vocabulary);
        concept.setExternalId("4282375");
        concept.setLabel("Unité stratigraphique");
        concept.setLangCode("fr");

        Person person = new Person();
        person.setId(1L);
        person.setUsername("someUsername");
        person.setPassword("somePassword");

        Institution institution = new Institution();
        institution.setId(1L);
        institution.setName("SIADev");
        institution.setManager(person);

        UserInfo userInfo = new UserInfo(institution, person, "fr");

        FullConceptDTO subConcept1 = new FullConceptDTO();
        subConcept1.setIdentifier(new PurlInfoDTO[]{ new PurlInfoDTO("string", "4282377") });
        subConcept1.setPrefLabel(new PurlInfoDTO[]{ new PurlInfoDTO("string", "Sub Concept 1", "fr") });

        FullConceptDTO subConcept2 = new FullConceptDTO();
        subConcept2.setIdentifier(new PurlInfoDTO[]{ new PurlInfoDTO("string", "4284785") });
        subConcept2.setPrefLabel(new PurlInfoDTO[]{ new PurlInfoDTO("string", "Sub Concept 2", "fr") });

        ConceptBranchDTO branchDTO = new ConceptBranchDTO();
        branchDTO.getData().put("4282377", subConcept1);
        branchDTO.getData().put("4284785", subConcept2);

        when(conceptApi.fetchDownExpansion(any(Vocabulary.class), anyString())).thenReturn(branchDTO);
        when(repository.findConceptByExternalIdIgnoreCase(anyString(), anyString())).thenReturn(Optional.empty());
        when(repository.save(any(Concept.class))).then(invocation -> invocation.getArgument(0));

        // Act
        List<Concept> result = conceptService.findSubConceptOf(userInfo, concept);

        // Assert
        assertThat(result)
                .hasSize(2)
                .allMatch(Objects::nonNull)
                .anyMatch(currentConcept -> currentConcept.getExternalId().equalsIgnoreCase("4282377"))
                .anyMatch(currentConcept -> currentConcept.getExternalId().equalsIgnoreCase("4284785"));
    }

}