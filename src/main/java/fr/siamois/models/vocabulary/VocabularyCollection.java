package fr.siamois.models.vocabulary;

import fr.siamois.models.ark.Ark;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "vocabulary_collection", schema = "public")
public class VocabularyCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vocabulary_collection_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_ark_id")
    private Ark ark;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    @NotNull
    @Column(name = "external_id", nullable = false, length = Integer.MAX_VALUE)
    private String externalId;

}