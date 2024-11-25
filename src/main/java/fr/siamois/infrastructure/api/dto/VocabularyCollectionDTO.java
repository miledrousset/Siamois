package fr.siamois.infrastructure.api.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO for the vocabulary collection. Structure of the API response for vocabulary collections.
 */
@Data
public class VocabularyCollectionDTO {
    private String idGroup;
    private List<LabelDTO> labels;
}
