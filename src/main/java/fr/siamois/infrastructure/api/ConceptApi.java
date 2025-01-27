package fr.siamois.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyCollection;
import fr.siamois.utils.builder.AutocompletionRequestBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service to fetch concept information from the API.
 * @author Julien Linget
 */
@Slf4j
@Service
public class ConceptApi {

    private final RestTemplate restTemplate;

    public ConceptApi(RequestFactory factory) {
        restTemplate = factory.buildRestTemplate();
    }

    /**
     * Fetch the autocomplete results of Opentheso API for a given input and vocabulary collection.
     * @param input The input to search for
     * @param lang The language to search in
     * @return A list of concept field DTOs
     */
    public List<ConceptFieldDTO> fetchAutocomplete(List<VocabularyCollection> collections, String input, String lang) {
        Vocabulary vocabulary = collections.get(0).getVocabulary();
        AutocompletionRequestBuilder builder = AutocompletionRequestBuilder
                .getBuilder(vocabulary.getBaseUri(), vocabulary.getExternalVocabularyId(), input);

        for (VocabularyCollection collection : collections)
            builder.withGroup(collection.getExternalId());

        builder.withLang(lang);

        return fetchAutocomplete(builder.build());
    }

    /**
     * Fetch the autocomplete results of Opentheso API for a given input. Filters the results by the given vocabulary.
     * @param vocabulary The vocabulary to search in
     * @param input The input to search for
     * @param lang The language to search in
     * @return A list of concept field DTOs for the given input
     */
    public List<ConceptFieldDTO> fetchAutocomplete(Vocabulary vocabulary, String input, String lang) {
        AutocompletionRequestBuilder builder = AutocompletionRequestBuilder
                .getBuilder(vocabulary.getBaseUri(), vocabulary.getExternalVocabularyId(), input);

        builder.withLang(lang);

        return fetchAutocomplete(builder.build());
    }

    /**
     * Fetch the autocomplete results of Opentheso API for a given uri.
     * @param requestUri The uri with the parameters to send to the API
     * @return A list of concept field DTOs
     */
    private List<ConceptFieldDTO> fetchAutocomplete(String requestUri) {
        URI uri = URI.create(requestUri);

        log.trace("Sending API request to {}", uri);

        String result = restTemplate.getForObject(uri, String.class);

        if (StringUtils.isEmpty(result)) {
            return new ArrayList<>();
        }

        log.trace("API response: {}", result);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return List.of(mapper.readValue(result, ConceptFieldDTO[].class));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private static class ConceptDTO {
        @JsonProperty("idConcept")
        private String idConcept;

        @JsonProperty("labels")
        private LabelDTO[] labels;
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

            ConceptDTO autocompleteParent = Arrays.stream(array)
                    .filter((conceptDTO1 -> Arrays.stream(conceptDTO1.labels).anyMatch((labelDTO -> labelDTO.getTitle().toUpperCase().contains("SIAAUTO")))))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Concept with code SIAAUTO not found"));

            uri = URI.create(vocabulary.getBaseUri() + String.format("/openapi/v1/concept/%s/%s/expansion?way=down", vocabulary.getExternalVocabularyId(), autocompleteParent.idConcept));

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);

            TypeReference<Map<String,FullConceptDTO>> typeReference = new TypeReference<>() {};

            Map<String, FullConceptDTO> result = mapper.readValue(response.getBody(), typeReference);

            ConceptBranchDTO branch = new ConceptBranchDTO();
            result.forEach(branch::addConceptBranchDTO);

            return branch;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while parsing branch", e);
        }
    }

}
