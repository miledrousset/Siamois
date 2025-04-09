package fr.siamois.domain.models.label;

import jakarta.persistence.*;
import org.hibernate.annotations.DiscriminatorFormula;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("CASE WHEN fk_concept_id IS NOT NULL THEN 'concept' " +
        "WHEN fk_vocabulary_id IS NOT NULL THEN 'vocabulary' " +
        "ELSE NULL END")
public abstract class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "label_id")
    protected Long id;

    @Column(name = "lang_code", nullable = false)
    protected String langCode;

    @Column(name = "label_value", nullable = false)
    protected String value;

}
