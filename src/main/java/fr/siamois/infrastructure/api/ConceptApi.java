package fr.siamois.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.models.vocabulary.Concept;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyCollection;
import fr.siamois.utils.builder.AutocompletionRequestBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

/**
 * Service to fetch concept information from the API.
 * @author Julien Linget
 */
@Slf4j
@Service
public class ConceptApi {

    private final RestTemplate restTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    public ConceptApi(RequestFactory factory) {
        restTemplate = factory.buildRestTemplate();
    }

    public ConceptBranchDTO fetchConceptsUnderTopTerm(Concept concept) {
        return fetchDownExpansion(concept.getVocabulary(), concept.getExternalId());
    }

    private ConceptBranchDTO fetchDownExpansion(Vocabulary vocabulary, String idConcept) {
        URI uri = URI.create(String.format("%s/openapi/v1/concept/%s/%s/expansion?way=down", vocabulary.getBaseUri(), vocabulary.getExternalVocabularyId(), idConcept));

        ResponseEntity<String> response = sendRequestAcceptJson(uri);

        TypeReference<Map<String,FullConceptDTO>> typeReference = new TypeReference<>() {};
        Map<String, FullConceptDTO> result = null;
        try {
            result = mapper.readValue(response.getBody(), typeReference);
            ConceptBranchDTO branch = new ConceptBranchDTO();
            result.forEach(branch::addConceptBranchDTO);
            return branch;
        } catch (JsonProcessingException e) {
            log.error("Error while processing JSON", e);
        }

        return new ConceptBranchDTO();
    }

    private static class ConceptDTO {
        @JsonProperty("idConcept")
        private String idConcept;

        @JsonProperty("labels")
        private LabelDTO[] labels;
    }

    public FullConceptDTO fetchConceptInfo(Vocabulary vocabulary, String conceptId) {
        URI uri = URI.create(vocabulary.getBaseUri() + String.format("/openapi/v1/concept/%s/%s", vocabulary.getExternalVocabularyId(), conceptId));
        ResponseEntity<String> response = sendRequestAcceptJson(uri);


        ObjectMapper mapper = new ObjectMapper();

        TypeReference<Map<String,FullConceptDTO>> typeReference = new TypeReference<>() {};

        try {
            Map<String, FullConceptDTO> result = mapper.readValue(response.getBody(), typeReference);
            return result.values().stream().findFirst().orElseThrow(() -> new RuntimeException("Invalid concept"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<String> sendRequestAcceptJson(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
    }

    private boolean isAutocompleteTopTerm(FullConceptDTO concept) {
        return concept.getNotation() != null
                && Arrays.stream(concept.getNotation())
                .anyMatch(notation -> notation.getValue().equalsIgnoreCase("SIAMOIS#SIAAUTO"));
    }

    private Optional<ConceptDTO> findAutocompleteTopTerm(Vocabulary vocabulary, ConceptDTO[] array) {
        for (ConceptDTO conceptDTO : array) {
            FullConceptDTO fullConceptDTO = fetchConceptInfo(vocabulary, conceptDTO.idConcept);
            if (isAutocompleteTopTerm(fullConceptDTO)) {
                return Optional.of(conceptDTO);
            }
        }
        return Optional.empty();
    }

    public ConceptBranchDTO fetchFieldsBranch(Vocabulary vocabulary) {
        URI uri = URI.create(vocabulary.getBaseUri() + String.format("/openapi/v1/thesaurus/%s/topconcept", vocabulary.getExternalVocabularyId()));

        String conceptDTO = restTemplate.getForObject(uri, String.class);
        if (conceptDTO == null) {
            throw new RuntimeException("Vocabulary not found");
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            ConceptDTO[] array = mapper.readValue(conceptDTO, ConceptDTO[].class);

            ConceptDTO autocompleteParent = findAutocompleteTopTerm(vocabulary, array).orElseThrow(() -> new IllegalArgumentException("Concept with notation SIAMOIS#SIAAUTO not found"));

            return fetchDownExpansion(vocabulary, autocompleteParent.idConcept);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while parsing branch", e);
        }
    }

}
