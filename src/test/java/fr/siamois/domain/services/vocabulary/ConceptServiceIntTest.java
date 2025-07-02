package fr.siamois.domain.services.vocabulary;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.exceptions.ErrorProcessingExpansionException;
import fr.siamois.domain.models.institution.Institution;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.database.repositories.vocabulary.ConceptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Tag("integration")
@ExtendWith(MockitoExtension.class)
class ConceptServiceIntTest {

    @Mock
    private ConceptRepository conceptRepository;

    @Mock
    private LabelService labelService;

    private ConceptService conceptService;

    private Vocabulary vocabulary;

    @BeforeEach
    void setUp() {
        ConceptApi conceptApi = new ConceptApi(new TestRestTemplate().getRestTemplate());
        conceptService = new ConceptService(conceptRepository, conceptApi, labelService);
        vocabulary = new Vocabulary();
        vocabulary.setId(1L);
        vocabulary.setExternalVocabularyId("th230");
        vocabulary.setBaseUri("https://thesaurus.mom.fr");
    }

    @Test
    void findDirectSubConceptOf() throws ErrorProcessingExpansionException {
        // Concept parent "Type d'unité d'enregistrement"
        Concept concept = new Concept();
        concept.setId(1L);
        concept.setVocabulary(vocabulary);
        concept.setExternalId("4282367");

        Person person = new Person();
        person.setId(1L);
        person.setUsername("someUsername");
        person.setPassword("somePassword");

        Institution institution = new Institution();
        institution.setId(1L);
        institution.setName("SIADev");
        institution.getManagers().add(person);

        when(conceptRepository.findConceptByExternalIdIgnoreCase(anyString(), anyString())).thenReturn(Optional.empty());
        when(conceptRepository.save(any(Concept.class))).then(invocationOnMock -> invocationOnMock.getArgument(0));

        List<Concept> result = conceptService.findDirectSubConceptOf(concept);

        assertThat(result)
                .hasSize(4)
                .allMatch(Objects::nonNull)
                .anyMatch(currentConcept -> currentConcept.getExternalId().equalsIgnoreCase("4282373"))
                .anyMatch(currentConcept -> currentConcept.getExternalId().equalsIgnoreCase("4282374"))
                .anyMatch(currentConcept -> currentConcept.getExternalId().equalsIgnoreCase("4282375"))
                .anyMatch(currentConcept -> currentConcept.getExternalId().equalsIgnoreCase("4282376"));
    }

    @Test
    void findDirectSubConceptOf_shouldOnlyReturnDirectChilds() throws ErrorProcessingExpansionException {
        Concept concept = new Concept();
        concept.setId(1L);
        concept.setVocabulary(vocabulary);
        concept.setExternalId("4283543");

        Person person = new Person();
        person.setId(1L);
        person.setUsername("someUsername");
        person.setPassword("somePassword");

        Institution institution = new Institution();
        institution.setId(1L);
        institution.setName("SIADev");
        institution.getManagers().add(person);

        List<String> unwantedId = List.of("4283550", "4283545", "4283546");

        when(conceptRepository.findConceptByExternalIdIgnoreCase(anyString(), anyString())).thenReturn(Optional.empty());
        when(conceptRepository.save(any(Concept.class))).then(invocationOnMock -> invocationOnMock.getArgument(0));

        List<Concept> result = conceptService.findDirectSubConceptOf(concept);

        assertThat(result)
                .hasSize(1)
                .allMatch(Objects::nonNull)
                .anyMatch(currentConcept -> currentConcept.getExternalId().equalsIgnoreCase("4283544"))
                .noneMatch(currentConcept -> unwantedId.contains(currentConcept.getExternalId()));
    }

}
