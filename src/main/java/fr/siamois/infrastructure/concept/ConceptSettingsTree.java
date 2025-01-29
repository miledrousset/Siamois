package fr.siamois.infrastructure.concept;

import fr.siamois.infrastructure.api.dto.ConceptBranchDTO;
import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import fr.siamois.services.vocabulary.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.util.*;

@Slf4j
@Component
@SessionScoped
public class ConceptSettingsTree {

    private final Map<String, ConceptNode> conceptsMap = new HashMap<>();
    private final FieldService fieldService;

    public ConceptSettingsTree(FieldService fieldService) {
        this.fieldService = fieldService;
    }


    public void buildTreeFromBranch(ConceptBranchDTO branch) {
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

        if (!treeIsValid()) {
            conceptsMap.clear();
            throw new IllegalStateException("Tree is not valid");
        }
    }

    public List<ConceptNode> searchConceptNodeForConfig(String configCode) {
        String[] parts = configCode.split("\\.");
        int currentIndex = 0;
        final String currentPartCode = parts[currentIndex];
        ConceptNode node = conceptsMap.values().stream()
                .filter((conceptNode -> conceptNode.getCode() != null))
                .filter((conceptNode -> conceptNode.getCode().equalsIgnoreCase(currentPartCode)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid config code: " + configCode));

        currentIndex++;

        while (currentIndex < parts.length - 1) {
            final String finalCurrentPartCode = parts[currentIndex];
            node = node.getChilds().stream()
                    .filter((conceptNode -> conceptNode.getCode() != null))
                    .filter(conceptNode -> conceptNode.getCode().equalsIgnoreCase(finalCurrentPartCode))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid config code: " + configCode));
            currentIndex++;
        }

        ConceptNode fieldNode = node.getChilds().get(0);

        return fieldNode.getChilds();
    }

    private Optional<ConceptNode> getParentOf(ConceptNode node) {
        for (Map.Entry<String, ConceptNode> entry : conceptsMap.entrySet()) {
            ConceptNode value = entry.getValue();

            if (value.getChilds().contains(node))
                return Optional.of(value);
        }

        return Optional.empty();
    }

    private List<ConceptNode> getLastNodesWithCode() {
        List<ConceptNode> nodes = new ArrayList<>();
        for (ConceptNode value : conceptsMap.values()) {
            if (genericNodeHasNoChildWithCode(value)) {
                nodes.add(value);
            }
        }
        return nodes;
    }

    private static boolean genericNodeHasNoChildWithCode(ConceptNode value) {
        boolean isGenericNode = value.getCode() != null;
        boolean hasNoChildWithCode = value.getChilds().stream()
                .filter((conceptNode -> {
                    String prefLabel = conceptNode.getConcept().getPrefLabel()[0].getValue();
                    return prefLabel.contains("(") && prefLabel.contains(")");
                }))
                .findFirst()
                .isEmpty();
        return isGenericNode && hasNoChildWithCode;
    }

    private List<String> getAllFieldCodes() {
        List<String> fieldCodes = new ArrayList<>();
        List<ConceptNode> leafs = getLastNodesWithCode();

        for (ConceptNode node : leafs) {
            StringBuilder codeBuilder = new StringBuilder(node.getCode());
            ConceptNode currentNode = node;

            while (!isRootNode(currentNode)) {
                log.trace("Current node code: {}", currentNode.getCode());
                Optional<ConceptNode> parent = getParentOf(currentNode);

                if (parent.isPresent()) {
                    ConceptNode parentNode = parent.get();

                    if (!isRootNode(parentNode)) {
                        codeBuilder.insert(0, ".")
                                .insert(0, parentNode.getCode());
                    }

                    currentNode = parentNode;

                } else {
                    return fieldCodes;
                }


            }

            fieldCodes.add(codeBuilder.toString());
        }

        return fieldCodes;
    }

    private static boolean isRootNode(ConceptNode currentNode) {
        return currentNode.getCode().equalsIgnoreCase("SIAAUTO");
    }

    public boolean treeIsValid() {
        List<String> treeFieldCodes = getAllFieldCodes().stream()
                .map(String::toUpperCase)
                .toList();

        log.trace("Tree field codes : {}", treeFieldCodes);

        List<String> codeFieldCodes = fieldService.searchAllFieldCodes().stream()
                .map(String::toUpperCase)
                .toList();

        log.trace("Code field codes: {}", codeFieldCodes);

        for (String fieldCode : treeFieldCodes) {
            if (!codeFieldCodes.contains(fieldCode)) {
                log.error("Field code {} does not exist in ", fieldCode);
                return false;
            }
        }

        return true;
    }

    public boolean isEmpty() {
        return conceptsMap.isEmpty();
    }

}
