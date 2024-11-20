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

@Slf4j
@Service
public class ThesaurusCollectionApi {

    public List<VocabularyCollectionDTO> fetchAllCollectionsFrom(String server, String vocabularyId) throws ClientSideErrorException {
        String url = String.format("%s/openapi/v1/group/%s", server, vocabularyId);
        RestTemplate restTemplate = new RestTemplate();
        try {
            VocabularyCollectionDTO[] data = restTemplate.getForObject(url, VocabularyCollectionDTO[].class);
            if (data == null) return new ArrayList<>();
            return Arrays.asList(data);
        } catch (RestClientException e) {
            log.error(e.getMessage(), e);
            throw new ClientSideErrorException(e.getMessage(), HttpStatusCode.valueOf(404));
        }
    }
}
