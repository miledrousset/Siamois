package fr.siamois.domain.models.form.customFormField;
import fr.siamois.domain.models.form.customForm.CustomForm;
import fr.siamois.domain.models.form.customField.CustomField;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class CustomFormFieldId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "fk_form_id", nullable = false)
    private CustomForm form;
    @ManyToOne
    @JoinColumn(name = "fk_field_id", nullable = false)
    private CustomField field;

}
