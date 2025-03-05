package fr.siamois.infrastructure.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for the thesaurus. Structure of the API response for thesaurus.
 * @author Julien Linget
 */
@Data
public class ThesaurusDTO {
    private String idTheso;
    private List<LabelDTO> labels;

    public ThesaurusDTO() {
        labels = new ArrayList<>();
    }

    public ThesaurusDTO(String idTheso, List<LabelDTO> labels) {
        this.idTheso = idTheso;
        this.labels = labels;
    }

    @JsonIgnore
    private String baseUri;

}
