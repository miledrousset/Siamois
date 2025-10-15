package fr.siamois.domain.models.vocabulary.label;

import fr.siamois.domain.models.vocabulary.Vocabulary;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Entity
@DiscriminatorValue("vocabulary")
@Data
public class VocabularyLabel extends Label {

    @ManyToOne
    @JoinColumn(name = "fk_vocabulary_id")
    private Vocabulary vocabulary;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VocabularyLabel that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(vocabulary, that.vocabulary) && super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), vocabulary);
    }
}
