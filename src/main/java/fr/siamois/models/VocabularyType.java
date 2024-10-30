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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vocabulary_type_id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "label", nullable = false, length = Integer.MAX_VALUE)
    private String label;

}