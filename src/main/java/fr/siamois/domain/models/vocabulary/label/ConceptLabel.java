package fr.siamois.domain.models.vocabulary.label;

import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Entity
@DiscriminatorValue("concept")
@Data
public class ConceptLabel extends Label {

    @ManyToOne
    @JoinColumn(name = "fk_concept_id")
    private Concept concept;

    public ConceptLabel() {}

    public ConceptLabel(String emptyLabelValue) {
        concept = new Concept();
        value = emptyLabelValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConceptLabel cl)) return false;

        return Objects.equals(concept, cl.concept) &&
                Objects.equals(value, cl.value)&&
                Objects.equals(langCode, cl.langCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(concept, value, langCode);
    }

}
