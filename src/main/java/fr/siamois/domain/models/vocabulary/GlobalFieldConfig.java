package fr.siamois.domain.models.vocabulary;

import fr.siamois.infrastructure.api.dto.FullConceptDTO;

import java.util.List;

public record GlobalFieldConfig(List<String> missingFieldCode, List<FullConceptDTO> conceptWithValidFieldCode) {
    public boolean isWrongConfig() {
        return !missingFieldCode.isEmpty();
    }

    public boolean isValid() {
        return missingFieldCode.isEmpty();
    }
}
