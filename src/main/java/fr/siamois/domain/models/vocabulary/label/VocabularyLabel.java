package fr.siamois.domain.models.vocabulary.label;

import fr.siamois.domain.models.vocabulary.Vocabulary;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("vocabulary")
public class VocabularyLabel extends Label {

    @ManyToOne
    @JoinColumn(name = "fk_vocabulary_id")
    private Vocabulary vocabulary;

}
