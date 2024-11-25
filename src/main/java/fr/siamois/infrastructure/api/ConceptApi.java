package fr.siamois.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.siamois.infrastructure.api.dto.ConceptFieldDTO;
import fr.siamois.models.vocabulary.VocabularyCollection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

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

    public List<ConceptFieldDTO> fetchAutocomplete(VocabularyCollection collection, String input, String lang) {
        String uri = String.format("%s/openapi/v1/concept/%s/autocomplete/%s?lang=%s&group=%s",
                collection.getVocabulary().getBaseUri(),
                collection.getVocabulary().getExternalVocabularyId(),
                input,
                lang,
                collection.getExternalId());

        String result = restTemplate.getForObject(uri, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return List.of(mapper.readValue(result, ConceptFieldDTO[].class));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

}
