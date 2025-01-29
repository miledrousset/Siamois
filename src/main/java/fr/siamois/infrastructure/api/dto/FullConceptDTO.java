package fr.siamois.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FullConceptDTO {
    @JsonProperty("http://purl.org/dc/terms/contributor")
    private PurlInfoDTO[] contributor;

    @JsonProperty("http://purl.org/dc/terms/created")
    private PurlInfoDTO[] created;

    @JsonProperty("http://purl.org/dc/terms/identifier")
    private PurlInfoDTO[] identifier;

    @JsonProperty("http://purl.org/dc/terms/modified")
    private PurlInfoDTO[] modified;

    @JsonProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
    private PurlInfoDTO[] type;

    @JsonProperty("http://www.w3.org/2004/02/skos/core#broader")
    private PurlInfoDTO[] broader;

    @JsonProperty("http://www.w3.org/2004/02/skos/core#inScheme")
    private PurlInfoDTO[] inScheme;

    @JsonProperty("http://www.w3.org/2004/02/skos/core#narrower")
    private PurlInfoDTO[] narrower;

    @JsonProperty("http://www.w3.org/2004/02/skos/core#prefLabel")
    private PurlInfoDTO[] prefLabel;

    @JsonProperty("http://purl.org/dc/terms/creator")
    private PurlInfoDTO[] creator;

    @JsonProperty("http://www.w3.org/2004/02/skos/core#topConceptOf")
    private PurlInfoDTO[] topConceptOf;

    @JsonProperty("http://www.w3.org/2004/02/skos/core#altLabel")
    private PurlInfoDTO[] altLabel;

    @JsonProperty("http://www.w3.org/2004/02/skos/core#notation")
    private PurlInfoDTO[] notation;

}
