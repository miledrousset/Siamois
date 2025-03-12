package fr.siamois.domain.models.form.customFormField;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "form_question")
@Entity
public class CustomFormField {

    @EmbeddedId
    private CustomFormFieldId id;


}