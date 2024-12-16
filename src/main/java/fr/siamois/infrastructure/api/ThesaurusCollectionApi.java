package fr.siamois.infrastructure.api;

import fr.siamois.infrastructure.api.dto.VocabularyCollectionDTO;
import fr.siamois.models.exceptions.api.ClientSideErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service to fetch thesaurus collection from the API.
 * @author Julien Linget
 */
@Slf4j
@Service
public class ThesaurusCollectionApi {

    private final RestTemplate restTemplate;

    public ThesaurusCollectionApi(RequestFactory requestFactory) {
        this.restTemplate = requestFactory.buildRestTemplate();
    }

    /**
     * Send a request to the API to fetch all collections from a vocabulary.
     * @param server The server URL
     * @param vocabularyId The vocabulary id
     * @return A list of vocabulary collection DTOs
     * @throws ClientSideErrorException Throws if the client sent wrong id or server URL
     */
    public List<VocabularyCollectionDTO> fetchAllCollectionsFrom(String server, String vocabularyId) throws ClientSideErrorException {
        String uri = String.format("%s/openapi/v1/group/%s", server, vocabularyId);
        try {
            VocabularyCollectionDTO[] data = restTemplate.getForObject(uri, VocabularyCollectionDTO[].class);
            if (data == null) return new ArrayList<>();
            return Arrays.asList(data);
        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw new ClientSideErrorException(e.getMessage(), HttpStatusCode.valueOf(404));
        }
    }
}
