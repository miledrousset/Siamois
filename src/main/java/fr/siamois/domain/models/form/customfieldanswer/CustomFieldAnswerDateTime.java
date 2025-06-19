package fr.siamois.domain.models.form.customfieldanswer;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;


@Data
@Entity
@DiscriminatorValue("DATETIME")
@Table(name = "custom_field_answer")
public class CustomFieldAnswerDateTime extends CustomFieldAnswer {

    @Column(name = "value_as_datetime")
    private OffsetDateTime value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomFieldAnswerDateTime that)) return false;
        if (!super.equals(o)) return false; // Ensures inherited fields are compared

        return Objects.equals(getPk(), that.getPk());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPk());
    }

}
