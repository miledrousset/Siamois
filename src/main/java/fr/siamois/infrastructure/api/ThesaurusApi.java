package fr.siamois.infrastructure.api;

import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service to fetch thesaurus information from the API.
 * @author Julien Linget
 */
@Service
public class ThesaurusApi {

    /**
     * Send a request to the API to fetch all public thesaurus names, ids and labels.
     * @param server The server URL
     * @return A list of thesaurus DTOs
     */
    public List<ThesaurusDTO> fetchAllPublicThesaurus(String server) {
        String uri = server + "/openapi/v1/thesaurus";
        RestTemplate restTemplate = new RestTemplate();
        ThesaurusDTO[] data = restTemplate.getForObject(uri, ThesaurusDTO[].class);
        if (data == null) return new ArrayList<>();
        return Arrays.asList(data);

    }

}
