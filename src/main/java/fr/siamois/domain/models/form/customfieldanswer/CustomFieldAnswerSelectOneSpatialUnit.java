package fr.siamois.domain.models.form.customfieldanswer;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;


@Data
@Entity
@DiscriminatorValue("SELECT_ONE_SPATIAL_UNIT")
@Table(name = "custom_field_answer")
public class CustomFieldAnswerSelectOneSpatialUnit extends CustomFieldAnswer {

    @ManyToOne
    @JoinColumn(name = "fk_value_as_spatial_unit")
    private SpatialUnit value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomFieldAnswerSelectOneSpatialUnit that)) return false;
        if (!super.equals(o)) return false; // Ensures any inherited fields are compared

        return Objects.equals(getPk(), that.getPk());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPk());
    }

}
