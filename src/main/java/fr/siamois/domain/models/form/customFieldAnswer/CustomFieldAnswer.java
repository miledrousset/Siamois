package fr.siamois.domain.models.form.customFieldAnswer;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.Formula;

@Data
@Table(name = "custom_field_answer")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("(SELECT f.answer_type FROM question f WHERE f.question_id = question_id)")
public abstract class CustomFieldAnswer {

    @EmbeddedId
    private CustomFieldAnswerId pk;

}