package fr.siamois.infrastructure.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class VocabularyCollectionDTO {
    private String idGroup;
    private List<LabelDTO> labels;
}
