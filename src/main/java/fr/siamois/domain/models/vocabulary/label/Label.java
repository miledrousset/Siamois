package fr.siamois.domain.models.vocabulary.label;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DiscriminatorFormula;

import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("CASE WHEN fk_concept_id IS NOT NULL THEN 'concept' " +
        "WHEN fk_vocabulary_id IS NOT NULL THEN 'vocabulary' " +
        "ELSE NULL END")
@Data
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fk_concept_id", "lang_code"}),
                @UniqueConstraint(columnNames = {"fk_vocabulary_id", "lang_code"})
        }
)
public abstract class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "label_id")
    protected Long id;

    @Column(name = "lang_code", nullable = false)
    protected String langCode;

    @Column(name = "label_value", nullable = false)
    protected String value;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Label label)) return false;
        return Objects.equals(langCode, label.langCode) && Objects.equals(value, label.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(langCode, value);
    }
}
