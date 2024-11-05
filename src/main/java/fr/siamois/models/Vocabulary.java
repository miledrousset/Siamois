package fr.siamois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vocabulary", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "vocabulary_ark_id_key", columnNames = {"ark_id"})
})
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vocabulary_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_type_id", nullable = false)
    private VocabularyType fkType;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ark_id", nullable = false)
    private Ark ark;

    @NotNull
    @Column(name = "vocabulary_name", nullable = false, length = Integer.MAX_VALUE)
    private String vocabularyName;

}