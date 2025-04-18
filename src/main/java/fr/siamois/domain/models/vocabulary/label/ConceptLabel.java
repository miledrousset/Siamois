package fr.siamois.domain.models.vocabulary.label;

import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("concept")
@Data
@EqualsAndHashCode(callSuper = true)
public class ConceptLabel extends Label {

    @ManyToOne
    @JoinColumn(name = "fk_concept_id")
    private Concept concept;

    public ConceptLabel() {}

    public ConceptLabel(String emptyLabelValue) {
        concept = new Concept();
        value = emptyLabelValue;
    }

}
