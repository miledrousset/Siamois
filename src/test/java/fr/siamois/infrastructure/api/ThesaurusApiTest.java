package fr.siamois.infrastructure.api;

import fr.siamois.domain.models.exceptions.api.InvalidEndpointException;
import fr.siamois.infrastructure.api.dto.LabelDTO;
import fr.siamois.infrastructure.api.dto.ThesaurusDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThesaurusApiTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RequestFactory requestFactory;

    private ThesaurusApi thesaurusApi;

    @BeforeEach
    void setUp() {
        when(requestFactory.buildRestTemplate(false)).thenReturn(this.restTemplate);
        thesaurusApi = new ThesaurusApi(requestFactory);
    }

    @Test
    void fetchAllPublicThesaurus_success() throws InvalidEndpointException {
        String server = "http://example.com";
        ThesaurusDTO[] thesaurusArray = {new ThesaurusDTO("1", List.of(new LabelDTO("fr", "Label1")))};
        when(restTemplate.getForObject(server + "/openapi/v1/thesaurus", ThesaurusDTO[].class)).thenReturn(thesaurusArray);

        List<ThesaurusDTO> result = thesaurusApi.fetchAllPublicThesaurus(server);

        assertEquals(1, result.size());
        assertEquals("Label1", result.get(0).getLabels().get(0).getTitle());
    }

    @Test
    void fetchAllPublicThesaurus_empty() throws InvalidEndpointException {
        String server = "http://example.com";
        when(restTemplate.getForObject(server + "/openapi/v1/thesaurus", ThesaurusDTO[].class)).thenReturn(null);

        List<ThesaurusDTO> result = thesaurusApi.fetchAllPublicThesaurus(server);

        assertTrue(result.isEmpty());
    }

    @Test
    void fetchAllPublicThesaurus_invalidEndpoint() {
        String server = "http://example.com";
        when(restTemplate.getForObject(server + "/openapi/v1/thesaurus", ThesaurusDTO[].class)).thenThrow(new RestClientException("Error"));

        assertThrows(InvalidEndpointException.class, () -> thesaurusApi.fetchAllPublicThesaurus(server));
    }
}