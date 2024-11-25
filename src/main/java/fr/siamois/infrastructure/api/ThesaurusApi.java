package fr.siamois.infrastructure.api;

import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ThesaurusApi {

    public Optional<ThesaurusDTO> fetchThesaurusInfos(String server, String thesaurusId) {
        return fetchAllPublicThesaurus(server).stream()
                .filter(thesaurusDTO -> thesaurusDTO.getIdTheso().equals(thesaurusId))
                .findFirst();
    }

    public List<ThesaurusDTO> fetchAllPublicThesaurus(String server) {
        String url = server + "/openapi/v1/thesaurus";
        RestTemplate restTemplate = new RestTemplate();
        ThesaurusDTO[] data = restTemplate.getForObject(url, ThesaurusDTO[].class);
        if (data == null) return new ArrayList<>();
        return Arrays.asList(data);

    }

}
