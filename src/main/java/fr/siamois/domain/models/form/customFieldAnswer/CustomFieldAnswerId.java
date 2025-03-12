package fr.siamois.domain.models.form.customFieldAnswer;

import fr.siamois.domain.models.form.CustomFormResponse;
import fr.siamois.domain.models.form.customField.CustomField;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class CustomFieldAnswerId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private CustomField question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_form_response_id", nullable = false)
    private CustomFormResponse formResponse;

    private int position; // Position is also part of the PK

}
