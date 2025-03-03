package fr.siamois.infrastructure.api.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ConceptBranchDTO {
    private final Map<String, FullConceptDTO> data = new HashMap<>();

    public void addConceptBranchDTO(String url, FullConceptDTO dto) {
        this.data.putIfAbsent(url, dto);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

}
