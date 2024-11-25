package fr.siamois.infrastructure.api.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO for the thesaurus. Structure of the API response for thesaurus.
 */
@Data
public class ThesaurusDTO {
    private String idTheso;
    private List<LabelDTO> labels;
}
