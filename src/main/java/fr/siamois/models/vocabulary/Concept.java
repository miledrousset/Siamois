package fr.siamois.models.vocabulary;

import fr.siamois.models.ark.Ark;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "concept")
public class Concept implements Serializable {
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

}