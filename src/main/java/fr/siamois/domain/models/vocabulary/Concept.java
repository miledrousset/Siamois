package fr.siamois.domain.models.vocabulary;

import fr.siamois.domain.models.ark.Ark;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@Table(name = "concept")
public class Concept implements Serializable {

    // Copy constructor
    public Concept(Concept concept) {
        this.id = concept.getId();
        this.ark = concept.getArk();
        this.label = concept.getLabel();
        this.vocabulary = concept.getVocabulary();
        this.externalId = concept.getExternalId();
        this.langCode = concept.getLangCode();
    }

    public Concept() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concept_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_ark_id")
    private Ark ark;

    @NotNull
    @Column(name = "label", nullable = false, length = Integer.MAX_VALUE)
    private String label;

    @Column(name = "external_id", length = Integer.MAX_VALUE)
    private String externalId;

    @NotNull
    @Column(name = "label_lang", length = Integer.MAX_VALUE)
    private String langCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Concept concept)) return false;

        return Objects.equals(ark, concept.ark) &&
                Objects.equals(externalId, concept.externalId) &&
                Objects.equals(vocabulary, concept.vocabulary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ark, externalId, vocabulary);
    }


}