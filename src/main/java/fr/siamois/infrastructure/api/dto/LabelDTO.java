package fr.siamois.infrastructure.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the label of a vocabulary. Structure of the API response for labels.
 * @author Julien Linget
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelDTO {
    private String lang;
    private String title;
}
