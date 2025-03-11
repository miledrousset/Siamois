package fr.siamois.domain.models.form.question.options;


import fr.siamois.domain.models.vocabulary.Concept;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SelectMultipleOptions implements Serializable {

    private List<Concept> conceptList;

}
