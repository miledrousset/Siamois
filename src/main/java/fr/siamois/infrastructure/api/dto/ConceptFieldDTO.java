package fr.siamois.infrastructure.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for the concept field. Structure of the API response for concept fields.
 */
@Getter
@Setter
@NoArgsConstructor
public class ConceptFieldDTO {
    private String uri;
    private String label;
}
