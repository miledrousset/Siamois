package fr.siamois.infrastructure.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for the thesaurus. Structure of the API response for thesaurus.
 * @author Julien Linget
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThesaurusDTO {
    private String idTheso;
    private List<LabelDTO> labels;
}
