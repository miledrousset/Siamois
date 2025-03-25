package fr.siamois.domain.models.form.customfieldanswer;

import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class CustomFieldAnswerId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "fk_field_id", nullable = false)
    private  CustomField field;

    @ManyToOne
    @JoinColumn(name = "fk_form_response", nullable = false)
    private  CustomFormResponse formResponse;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomFieldAnswerId that)) return false;

        return Objects.equals(field, that.field) &&
                Objects.equals(formResponse, that.formResponse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, formResponse);
    }

}
