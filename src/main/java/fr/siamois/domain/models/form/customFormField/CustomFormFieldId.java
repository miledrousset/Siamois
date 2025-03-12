package fr.siamois.domain.models.form.customFormField;
import fr.siamois.domain.models.form.CustomForm;
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
    @JoinColumn(name = "form_id", nullable = false)
    private CustomForm form;
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private CustomField question;
    private int position; // Position is also part of the PK


}
