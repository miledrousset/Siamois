package fr.siamois.infrastructure.concept;

import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConceptNode {

    private final String code;
    private final FullConceptDTO concept;
    private final List<ConceptNode> childs = new ArrayList<>();

    public ConceptNode(FullConceptDTO concept) {
        this.concept = concept;

        if (concept.getNotation().length > 0 && concept.getNotation()[0] != null) {
            code = concept.getNotation()[0].getValue();
        } else {
            code = null;
        }


    }

}
