package fr.siamois.models.vocabulary;

import fr.siamois.models.ark.Ark;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "concept")
public class Concept {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concept_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    private Ark ark;

    @NotNull
    @Column(name = "label", nullable = false, length = Integer.MAX_VALUE)
    private String label;

}