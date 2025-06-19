package fr.siamois.domain.models.form.customfieldanswer;

import fr.siamois.domain.models.auth.Person;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;


@Data
public abstract class CustomFieldAnswerSelectPerson extends CustomFieldAnswer {


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomFieldAnswerSelectPerson that)) return false;
        if (!super.equals(o)) return false; // Ensures any inherited fields are compared

        return Objects.equals(getPk(), that.getPk());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPk());
    }

}
