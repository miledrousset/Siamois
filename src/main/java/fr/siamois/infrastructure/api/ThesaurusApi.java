package fr.siamois.infrastructure.api;

import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.*;

/**
 * Service to fetch thesaurus information from the API.
 *
 * @author Julien Linget
 */
@Slf4j
@Service
public class ThesaurusApi {

    private final RestTemplate restTemplate;

    public ThesaurusApi(RequestFactory requestFactory) {
        this.restTemplate = requestFactory.buildRestTemplate(false);
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

    public ThesaurusDTO fetchThesaurusInfo(String uri) throws IOException, InvalidEndpointException {
        URI uriObj = URI.create(uri);
        uriObj = findRedirectUriIfArk(uriObj);

        String[] data = uriObj.toString().split("\\?idt=");
        String host = data[0];
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }
        String externalId = data[1];

        Optional<ThesaurusDTO> result = fetchThesaurusInfo(host, externalId);
        if (result.isEmpty()) {
            throw new InvalidEndpointException(
                    String.format("Could not fetch thesaurus info of %s from the API %s", externalId ,host)
            );
        }
        return result.get();
    }

    private URI findRedirectUriIfArk(URI uriObj) {
        if (isNotUriWithIdtParameters(uriObj)) {
            HttpEntity<String> entity = restTemplate.getForEntity(uriObj.toString(), String.class);
            if (entity.getHeaders().getLocation() != null) {
                uriObj = entity.getHeaders().getLocation();
            }
        }
        return uriObj;
    }

    private static boolean isNotUriWithIdtParameters(URI uriObj) {
        return uriObj.getRawQuery() == null || uriObj.getRawQuery().isEmpty() || !uriObj.getRawQuery().contains("idt");
    }

    public Optional<ThesaurusDTO> fetchThesaurusInfo(String server, String idThesaurus) throws InvalidEndpointException {
        List<ThesaurusDTO> publicThesaurus = fetchAllPublicThesaurus(server);

        Optional<ThesaurusDTO> result = publicThesaurus.stream()
                .filter(thesaurus -> thesaurus.getIdTheso().equalsIgnoreCase(idThesaurus))
                .findFirst();

        result.ifPresent(thesaurusDTO -> thesaurusDTO.setBaseUri(server));

        return result;
    }

}
