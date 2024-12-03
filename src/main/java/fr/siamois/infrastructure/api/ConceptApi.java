package fr.siamois.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.vocabulary.Vocabulary;
import fr.siamois.models.vocabulary.VocabularyCollection;
import fr.siamois.utils.builder.AutocompletionRequestBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to fetch concept information from the API.
 * @author Julien Linget
 */
@Slf4j
@Service
public class ConceptApi {

    private final RestTemplate restTemplate;

    public ConceptApi(RestTemplateBuilder builder) {
        this.restTemplate = builder.requestFactory(() -> new SimpleClientHttpRequestFactory() {
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                super.prepareConnection(connection, httpMethod);
                connection.setInstanceFollowRedirects(true);
            }
        }).build();
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

}
