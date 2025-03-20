package fr.siamois.domain.models.form.customFieldAnswer;

import fr.siamois.domain.models.form.customField.CustomField;
import fr.siamois.domain.models.form.customFormResponse.CustomFormResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class CustomFieldAnswerId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "fk_field_id", nullable = false)
    private CustomField field;

    @ManyToOne
    @JoinColumn(name = "fk_form_response", nullable = false)
    private CustomFormResponse formResponse;

}
