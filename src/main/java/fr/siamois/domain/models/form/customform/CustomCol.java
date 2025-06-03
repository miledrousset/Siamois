package fr.siamois.domain.models.form.customform;

import fr.siamois.domain.models.form.customfield.CustomField;
import lombok.Data;


@Data
public class CustomCol {

    private CustomField field;
    private String className;

}
