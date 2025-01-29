package fr.siamois.infrastructure.concept;

import fr.siamois.infrastructure.api.dto.FullConceptDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class ConceptNode {

    private final String code;
    private final FullConceptDTO concept;
    private final List<ConceptNode> childs = new ArrayList<>();

    public ConceptNode(FullConceptDTO concept) {
        this.concept = concept;

        String regex = "\\(([^)]+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(concept.getPrefLabel()[0].getValue());
        if (matcher.find()) {
            code = matcher.group(1);
        } else {
            code = null;
        }
    }

}
