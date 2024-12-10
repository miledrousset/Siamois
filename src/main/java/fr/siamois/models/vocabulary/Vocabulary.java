package fr.siamois.models.vocabulary;

import fr.siamois.models.ark.Ark;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "vocabulary", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "vocabulary_pk_uri_external_id", columnNames = {"base_uri", "external_id"})
})
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vocabulary_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_type_id", nullable = false)
    private VocabularyType type;

    @NotNull
    @Column(name = "vocabulary_name", nullable = false, length = Integer.MAX_VALUE)
    private String vocabularyName;

    @NotNull
    @Column(name = "external_id", nullable = false)
    private String externalVocabularyId;

    @NotNull
    @Column(name = "base_uri", nullable = false)
    private String baseUri;

}