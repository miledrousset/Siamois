package fr.siamois.infrastructure.api;

import fr.siamois.infrastructure.api.dto.VocabularyCollectionDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ThesaurusCollectionApi {

    public List<VocabularyCollectionDTO> fetchAllCollectionsFrom(String server, String vocabularyId) {
        String url = String.format("%s/openapi/v1/group/%s", server, vocabularyId);
        RestTemplate restTemplate = new RestTemplate();
        VocabularyCollectionDTO[] data = restTemplate.getForObject(url, VocabularyCollectionDTO[].class);
        if (data == null) return new ArrayList<>();
        return Arrays.asList(data);
    }


}
