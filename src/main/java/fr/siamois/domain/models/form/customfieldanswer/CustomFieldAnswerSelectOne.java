package fr.siamois.domain.models.form.customfieldanswer;

import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;


@Data
@Entity
@DiscriminatorValue("SELECT_ONE")
@Table(name = "custom_field_answer")
public class CustomFieldAnswerSelectOne extends CustomFieldAnswer {

    @ManyToOne
    @JoinColumn(name = "fk_value_as_concept")
    private Concept value;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomFieldAnswerSelectOne that)) return false;
        if (!super.equals(o)) return false; // Ensures any inherited fields are compared

        return Objects.equals(getPk(), that.getPk());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPk());
    }

}
