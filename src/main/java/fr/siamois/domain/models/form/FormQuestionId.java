package fr.siamois.domain.models.form;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class FormQuestionId implements Serializable {

    private Long formId;
    private Long questionId;
    private int position; // Position is also part of the PK

    public FormQuestionId() {}

    public FormQuestionId(Long formId, Long questionId, int position) {
        this.formId = formId;
        this.questionId = questionId;
        this.position = position;
    }

    // Getters and Setters

    // Implement hashCode() and equals() for correct behavior in collections
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormQuestionId that = (FormQuestionId) o;
        return position == that.position &&
                Objects.equals(formId, that.formId) &&
                Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formId, questionId, position);
    }
}
