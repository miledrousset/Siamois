package fr.siamois.domain.models.form;
import fr.siamois.domain.models.form.question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class FormQuestionId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    private int position; // Position is also part of the PK


}
