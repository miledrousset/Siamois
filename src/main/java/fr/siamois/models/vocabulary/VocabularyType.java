package fr.siamois.models.vocabulary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "vocabulary_type", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "vocabulary_type_label_key", columnNames = {"label"})
})
public class VocabularyType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vocabulary_type_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "label", nullable = false, length = Integer.MAX_VALUE)
    private String label;

}