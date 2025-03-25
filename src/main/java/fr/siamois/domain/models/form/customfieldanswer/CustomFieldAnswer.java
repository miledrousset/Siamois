package fr.siamois.domain.models.form.customfieldanswer;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

@Data
@Table(name = "custom_field_answer")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "answer_type", discriminatorType = DiscriminatorType.STRING)
public abstract class CustomFieldAnswer {

    @EmbeddedId
    private CustomFieldAnswerId pk;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomFieldAnswer that)) return false;

        return Objects.equals(pk, that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk);
    }

}