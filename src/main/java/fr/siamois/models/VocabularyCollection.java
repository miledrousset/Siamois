package fr.siamois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "vocabulary_collection", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "vocabulary_collection_fk_ark_id_key", columnNames = {"fk_ark_id"})
})
public class VocabularyCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vocabulary_collection_id", nullable = false)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    private Ark ark;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    @NotNull
    @Column(name = "external_id", nullable = false, length = Integer.MAX_VALUE)
    private String externalId;

}