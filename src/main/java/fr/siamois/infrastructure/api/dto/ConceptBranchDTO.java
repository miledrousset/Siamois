package fr.siamois.infrastructure.api.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ConceptBranchDTO {
    private final Map<String, FullInfoDTO> data = new HashMap<>();

    public void addConceptBranchDTO(String url, FullInfoDTO dto) {
        this.data.putIfAbsent(url, dto);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

}
