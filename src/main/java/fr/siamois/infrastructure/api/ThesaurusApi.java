package fr.siamois.infrastructure.api;

import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;

@Service
public class ThesaurusApi {

    public Optional<ThesaurusDTO> fetchThesaurusInfos(String server, String thesaurusId) {
        String url = server + "/openapi/v1/thesaurus";
        RestTemplate restTemplate = new RestTemplate();
        ThesaurusDTO[] data = restTemplate.getForObject(url, ThesaurusDTO[].class);

        if (data == null) return Optional.empty();

        return Arrays.stream(data)
                .filter(thesaurusDTO -> thesaurusDTO.getIdTheso().equals(thesaurusId))
                .findFirst();
    }

}
