package fr.siamois.domain.models.vocabulary.label;

import fr.siamois.domain.models.vocabulary.Vocabulary;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("vocabulary")
@Data
@EqualsAndHashCode(callSuper = true)
public class VocabularyLabel extends Label {

    @ManyToOne
    @JoinColumn(name = "fk_vocabulary_id")
    private Vocabulary vocabulary;

}
