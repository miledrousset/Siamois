package fr.siamois.domain.models.form.customFieldAnswer;

import fr.siamois.domain.models.ReferencableEntity;
import fr.siamois.domain.models.form.customFormResponse.CustomFormResponse;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import jakarta.persistence.*;
import lombok.Data;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.Formula;

@Data
@Table(name = "custom_field_answer")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("(SELECT f.answer_type FROM custom_field f WHERE f.custom_field_id = fk_field_id)")
public abstract class CustomFieldAnswer {

    @EmbeddedId
    private CustomFieldAnswerId pk;

}