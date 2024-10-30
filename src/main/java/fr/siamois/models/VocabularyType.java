package fr.siamois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vocabulary_type")
public class VocabularyType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vocabulary_type_id_gen")
    @SequenceGenerator(name = "vocabulary_type_id_gen", sequenceName = "vocabulary_type_vocabulary_type_id_seq", allocationSize = 1)
    @Column(name = "vocabulary_type_id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "label", nullable = false, length = Integer.MAX_VALUE)
    private String label;

}