package fr.siamois.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.domain.models.exceptions.api.NotSiamoisThesaurusException;
import fr.siamois.domain.models.vocabulary.Concept;
import fr.siamois.domain.models.vocabulary.Vocabulary;
import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.FullInfoDTO;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.domain.models.exceptions.ErrorProcessingExpansionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service to fetch concept information from the API.
 * @author Julien Linget
 */
@Slf4j
@Service
public class ConceptApi {

    private final RestTemplate restTemplate;

    private final ObjectMapper mapper;

    @Autowired
    public ConceptApi(RequestFactory factory) {
        restTemplate = factory.buildRestTemplate(true);
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ConceptApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    ConceptApi(RequestFactory factory, ObjectMapper mapper) {
        this.restTemplate = factory.buildRestTemplate(true);
        this.mapper = mapper;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ConceptBranchDTO fetchConceptsUnderTopTerm(Concept concept) throws ErrorProcessingExpansionException {
        return fetchDownExpansion(concept.getVocabulary(), concept.getExternalId());
    }

    public ConceptBranchDTO fetchDownExpansion(Vocabulary vocabulary, String idConcept) throws ErrorProcessingExpansionException {
        URI uri = URI.create(String.format("%s/openapi/v1/concept/%s/%s/expansion?way=down", vocabulary.getBaseUri(), vocabulary.getExternalVocabularyId(), idConcept));

        ResponseEntity<String> response = sendRequestAcceptJson(uri);

        TypeReference<Map<String, FullInfoDTO>> typeReference = new TypeReference<>() {};
        Map<String, FullInfoDTO> result;
        try {
            result = mapper.readValue(response.getBody(), typeReference);
            ConceptBranchDTO branch = new ConceptBranchDTO();
            result.forEach(branch::addConceptBranchDTO);
            return branch;
        } catch (JsonProcessingException e) {
            log.error("Error while processing JSON", e);
            throw new ErrorProcessingExpansionException("Error while processing JSON for expansion");
        }
    }

    static class ConceptDTO {
        @JsonProperty("idConcept")
        String idConcept;

        @JsonProperty("labels")
        LabelDTO[] labels;
    }

    public FullInfoDTO fetchConceptInfo(Vocabulary vocabulary, String conceptId) {
        URI uri = URI.create(vocabulary.getBaseUri() + String.format("/openapi/v1/concept/%s/%s", vocabulary.getExternalVocabularyId(), conceptId));
        ResponseEntity<String> response = sendRequestAcceptJson(uri);

        TypeReference<Map<String, FullInfoDTO>> typeReference = new TypeReference<>() {};

        try {
            Map<String, FullInfoDTO> result = mapper.readValue(response.getBody(), typeReference);
            return result.values().stream().findFirst().orElseThrow(() -> new RuntimeException("Invalid concept"));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private ResponseEntity<String> sendRequestAcceptJson(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
    }

    private boolean isAutocompleteTopTerm(FullInfoDTO concept) {
        return concept != null && concept.getNotation() != null
                && Arrays.stream(concept.getNotation())
                .anyMatch(notation -> notation.getValue().equalsIgnoreCase("SIAMOIS#SIAAUTO"));
    }

    private Optional<ConceptDTO> findAutocompleteTopTerm(Vocabulary vocabulary, ConceptDTO[] array) {
        for (ConceptDTO conceptDTO : array) {
            FullInfoDTO fullInfoDTO = fetchConceptInfo(vocabulary, conceptDTO.idConcept);
            if (isAutocompleteTopTerm(fullInfoDTO)) {
                return Optional.of(conceptDTO);
            }
        }
        return Optional.empty();
    }

    public ConceptBranchDTO fetchFieldsBranch(Vocabulary vocabulary) throws NotSiamoisThesaurusException, ErrorProcessingExpansionException {
        URI uri = URI.create(vocabulary.getBaseUri() + String.format("/openapi/v1/thesaurus/%s/topconcept", vocabulary.getExternalVocabularyId()));

        String conceptDTO = restTemplate.getForObject(uri, String.class);
        if (conceptDTO == null) {
            log.error("Vocabulary not found");
            return null;
        }

        try {
            ConceptDTO[] array = mapper.readValue(conceptDTO, ConceptDTO[].class);

            ConceptDTO autocompleteParent = findAutocompleteTopTerm(vocabulary, array)
                    .orElseThrow(() -> new NotSiamoisThesaurusException("Concept with notation SIAMOIS#SIAAUTO not found in thesaurus %s",
                            vocabulary.getExternalVocabularyId()));

            return fetchDownExpansion(vocabulary, autocompleteParent.idConcept);

        } catch (JsonProcessingException e) {
            log.error("Error while parsing branch", e);
            throw new ErrorProcessingExpansionException("Error while parsing branch");
        }
    }

}
