package fr.siamois.domain.models.form.customfield;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
@Entity
@DiscriminatorValue("SELECT_ONE_FROM_FIELD_CODE")
@Table(name = "custom_field")
public class CustomFieldSelectOneFromFieldCode extends CustomField {

    @Column(name = "field_code")
    private String fieldCode ;

    private String iconClass ;
    private String styleClass ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomFieldSelectOneFromFieldCode that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(fieldCode, that.fieldCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fieldCode);
    }


}
