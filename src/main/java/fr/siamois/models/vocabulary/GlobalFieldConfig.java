package fr.siamois.models.vocabulary;

import fr.siamois.infrastructure.api.dto.FullConceptDTO;

import java.util.List;

public record GlobalFieldConfig(List<String> missingFieldCode, List<String> notExistingFieldCode, List<FullConceptDTO> conceptWithValidFieldCode) {
    public boolean isWrongConfig() {
        return !missingFieldCode.isEmpty() || !notExistingFieldCode.isEmpty();
    }

    public boolean isValid() {
        return missingFieldCode.isEmpty() && notExistingFieldCode.isEmpty();
    }
}
