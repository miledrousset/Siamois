package fr.siamois.domain.models.vocabulary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@Table(name = "concept")
@NoArgsConstructor
public class Concept implements Serializable {

    // Copy constructor
    public Concept(Concept concept) {
        this.id = concept.getId();
        this.vocabulary = concept.getVocabulary();
        this.externalId = concept.getExternalId();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concept_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    @Column(name = "external_id", length = Integer.MAX_VALUE)
    private String externalId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Concept concept)) return false;

        return Objects.equals(externalId, concept.externalId) &&
                Objects.equals(vocabulary, concept.vocabulary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, vocabulary);
    }


}