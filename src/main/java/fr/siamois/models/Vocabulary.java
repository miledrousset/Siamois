package fr.siamois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vocabulary")
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vocabulary_id_gen")
    @SequenceGenerator(name = "vocabulary_id_gen", sequenceName = "vocabulary_vocabulary_id_seq", allocationSize = 1)
    @Column(name = "vocabulary_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_type_id", nullable = false)
    private fr.siamois.models.VocabularyType fkType;

    @NotNull
    @Column(name = "uri", nullable = false, length = Integer.MAX_VALUE)
    private String uri;

    @NotNull
    @Column(name = "ark_resolver", nullable = false, length = Integer.MAX_VALUE)
    private String arkResolver;

    @NotNull
    @Column(name = "vocabulary_name", nullable = false, length = Integer.MAX_VALUE)
    private String vocabularyName;

}