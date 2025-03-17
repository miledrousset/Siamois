package fr.siamois.domain.models.form.customField;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "custom_field")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "answer_type", discriminatorType = DiscriminatorType.STRING)
public abstract class CustomField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_field_id", nullable = false)
    private Long id;

    @Column(name = "label")
    private String label;

    @Column(name = "hint")
    private String hint;

}
