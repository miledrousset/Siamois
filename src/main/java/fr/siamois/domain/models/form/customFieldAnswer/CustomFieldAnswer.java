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
@DiscriminatorColumn(name = "answer_type", discriminatorType = DiscriminatorType.STRING)
public abstract class CustomFieldAnswer {

    @EmbeddedId
    private CustomFieldAnswerId pk;

}