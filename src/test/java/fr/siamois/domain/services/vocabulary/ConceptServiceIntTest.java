package fr.siamois.domain.services.vocabulary;

import fr.siamois.domain.models.Institution;
import fr.siamois.domain.models.UserInfo;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.infrastructure.api.ConceptApi;
import fr.siamois.infrastructure.repositories.vocabulary.ConceptRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Tag("integration")
@ExtendWith(MockitoExtension.class)
public class ConceptServiceIntTest {

    @Mock
    private ConceptRepository conceptRepository;

    private ConceptService conceptService;

    @BeforeEach
    void setUp() {
        ConceptApi conceptApi = new ConceptApi(new TestRestTemplate().getRestTemplate());
        conceptService = new ConceptService(conceptRepository, conceptApi);
    }

    @Test
    public void findSubConceptOf() {
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

        when(conceptRepository.findConceptByExternalIdIgnoreCase(anyString(), anyString())).thenReturn(Optional.empty());
        when(conceptRepository.save(any(Concept.class))).then(invocationOnMock -> invocationOnMock.getArgument(0));

        List<Concept> result = conceptService.findSubConceptOf(userInfo, concept);

        assertThat(result)
                .hasSize(2)
                .allMatch(Objects::nonNull)
                .anyMatch(currentConcept -> currentConcept.getExternalId().equalsIgnoreCase("4282377"))
                .anyMatch(currentConcept -> currentConcept.getExternalId().equalsIgnoreCase("4284785"));
    }

}
