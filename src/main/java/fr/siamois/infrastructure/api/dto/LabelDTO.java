package fr.siamois.infrastructure.api.dto;

import lombok.Data;

/**
 * DTO for the label of a vocabulary. Structure of the API response for labels.
 */
@Data
public class LabelDTO {
    private String lang;
    private String title;
}
