package fr.siamois.infrastructure.api;

import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service to fetch thesaurus information from the API.
 *
 * @author Julien Linget
 */
@Service
public class ThesaurusApi {

    private final RestTemplate restTemplate;

    public ThesaurusApi(RequestFactory requestFactory) {
        this.restTemplate = requestFactory.buildRestTemplate();
    }

    /**
     * Send a request to the API to fetch all public thesaurus names, ids and labels.
     *
     * @param server The server URL
     * @return A list of thesaurus DTOs
     */
    public List<ThesaurusDTO> fetchAllPublicThesaurus(String server) throws InvalidEndpointException {
        String uri = server + "/openapi/v1/thesaurus";
        try {
            ThesaurusDTO[] data = restTemplate.getForObject(uri, ThesaurusDTO[].class);
            if (data == null) return new ArrayList<>();
            return Arrays.asList(data);
        } catch (RestClientException | IllegalArgumentException e) {
            throw new InvalidEndpointException("Could not fetch thesaurus data from the API");
        }
    }

}
