package fr.siamois.domain.models.form.customfield;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;


@Getter
@Setter
@Entity
@Table(name = "custom_field")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "answer_type", discriminatorType = DiscriminatorType.STRING)
public abstract class CustomField implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_field_id", nullable = false)
    private Long id;

    @Column(name = "label")
    private String label; // label or label code if system field

    // System or custom field
    @Column(name = "is_system_field")
    private Boolean isSystemField;

    @Column(name = "hint")
    private String hint;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_concept")
    private Concept concept;

    @Column(name = "value_binding")
    private String valueBinding; // e.g. "creationTime", "authors" to bind system field with JPA entities attributes

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_unit_concept")
    private Concept unitConcept;

    @Column(name = "unit_label")
    private String unitLabel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_author")
    private Person author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomField that = (CustomField) o;
        return Objects.equals(concept, that.concept) &&
                Objects.equals(unitConcept, that.unitConcept) &&
                Objects.equals(unitLabel, that.unitLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(concept, unitConcept, unitLabel);
    }

}
