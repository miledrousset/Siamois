package fr.siamois.domain.models.form.customFormField;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "custom_form_field")
@Entity
public class CustomFormField {

    @EmbeddedId
    private CustomFormFieldId id;

    @Column(name="required")
    private Boolean required;


}