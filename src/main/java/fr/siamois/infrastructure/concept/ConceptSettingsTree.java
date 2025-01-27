package fr.siamois.infrastructure.concept;

import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConceptSettingsTree {

    private final Map<String, ConceptNode> conceptsMap = new HashMap<>();

    public ConceptSettingsTree(ConceptBranchDTO branch) {
        buildTree(branch);
    }

    private void buildTree(ConceptBranchDTO branch) {
        for (Map.Entry<String, FullConceptDTO> entry : branch.getData().entrySet()) {
            String key = entry.getKey();
            FullConceptDTO fullConceptDTO = entry.getValue();
            conceptsMap.putIfAbsent(key, new ConceptNode(fullConceptDTO));
        }

        for (Map.Entry<String, FullConceptDTO> entry : branch.getData().entrySet()) {
            String key = entry.getKey();
            FullConceptDTO fullConceptDTO = entry.getValue();
            ConceptNode child = conceptsMap.get(key);
            if (fullConceptDTO.getBroader() != null) {
                ConceptNode parent = conceptsMap.get(fullConceptDTO.getBroader()[0].getValue());
                parent.getChilds().add(child);
            }
        }
    }

    public List<ConceptNode> searchConceptNodeForConfig(String configCode) {
        String[] parts = configCode.split("\\.");
        int currentIndex = 0;
        final String currentPartCode = parts[currentIndex];
        ConceptNode node = conceptsMap.values().stream()
                .filter((conceptNode -> conceptNode.getCode().equalsIgnoreCase(currentPartCode)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid config code: " + configCode));

        currentIndex++;

        while (currentIndex < parts.length - 1) {
            final String finalCurrentPartCode = parts[currentIndex];
            node = node.getChilds().stream()
                    .filter(conceptNode -> conceptNode.getCode().equalsIgnoreCase(finalCurrentPartCode))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid config code: " + configCode));
            currentIndex++;
        }

        ConceptNode fieldNode = node.getChilds().get(0);

        return fieldNode.getChilds();
    }



}
