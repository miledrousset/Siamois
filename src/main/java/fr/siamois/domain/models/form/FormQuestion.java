package fr.siamois.domain.models.form;

import fr.siamois.domain.models.form.question.Question;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "form_question")
@Entity
public class FormQuestion {

    @EmbeddedId
    private FormQuestionId id;


}