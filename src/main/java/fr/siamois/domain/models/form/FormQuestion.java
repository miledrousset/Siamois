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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("formId")
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @ManyToOne
    @MapsId("questionId")
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(insertable=false, updatable=false)
    private int position; // The position of the question in the form

}