package fr.siamois.domain.models.form.question;

import fr.siamois.domain.models.form.question.options.IntegerOptions;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;



@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("INTEGER")
@Table(name = "question")
public class QuestionInteger extends Question {

    @Column(name = "options", columnDefinition = "jsonb")
    private IntegerOptions options;

}
