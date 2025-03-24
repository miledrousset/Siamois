package fr.siamois.domain.models.vocabulary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "vocabulary_type")
public class VocabularyType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vocabulary_type_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "label", nullable = false, length = Integer.MAX_VALUE, unique = true)
    private String label;

}