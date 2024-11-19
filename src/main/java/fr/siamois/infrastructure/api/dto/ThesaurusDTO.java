package fr.siamois.infrastructure.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class ThesaurusDTO {
    private String idTheso;
    private List<LabelDTO> labels;
}
