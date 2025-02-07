package fr.siamois.infrastructure.api;

import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.models.exceptions.NotSiamoisThesaurusException;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConceptApiTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RequestFactory requestFactory;

    private ConceptApi conceptApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(requestFactory.buildRestTemplate()).thenReturn(restTemplate);
        conceptApi = new ConceptApi(requestFactory);
    }

    @Test
    void fetchConceptsUnderTopTerm() {
        Concept concept = new Concept();
        concept.setExternalId("testId");
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setBaseUri("http://example.com");
        vocabulary.setExternalVocabularyId("vocabId");
        concept.setVocabulary(vocabulary);

        ConceptBranchDTO expectedBranch = new ConceptBranchDTO();
        FullConceptDTO fullConceptDTO = new FullConceptDTO();
        expectedBranch.addConceptBranchDTO("testUrl", fullConceptDTO);

        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"testUrl\": {}}", HttpStatus.OK));

        ConceptBranchDTO result = conceptApi.fetchConceptsUnderTopTerm(concept);

        assertEquals(expectedBranch.getData().size(), result.getData().size());
    }

    @Test
    void fetchConceptInfo() {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setBaseUri("http://example.com");
        vocabulary.setExternalVocabularyId("vocabId");

        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("{\"testId\": {}}", HttpStatus.OK));

        FullConceptDTO result = conceptApi.fetchConceptInfo(vocabulary, "testId");

        assertNotNull(result);
    }

    @Test
    void fetchFieldsBranch() throws NotSiamoisThesaurusException, IOException {
        Vocabulary vocabulary = new Vocabulary();
        vocabulary.setBaseUri("http://example.com");
        vocabulary.setExternalVocabularyId("th223");

        String baseInfo = Files.readString(Path.of("src/test/resources/json/topconcept_baseinfo.json"), StandardCharsets.UTF_8);
        String completeInfo = Files.readString(Path.of("src/test/resources/json/topconcept_full.json"), StandardCharsets.UTF_8);

        when(restTemplate.getForObject(any(URI.class), eq(String.class))).thenReturn(baseInfo);

        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(completeInfo, HttpStatus.OK));

        ConceptBranchDTO result = conceptApi.fetchFieldsBranch(vocabulary);

        assertNotNull(result);
    }
}