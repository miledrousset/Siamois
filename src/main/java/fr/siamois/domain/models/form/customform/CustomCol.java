package fr.siamois.domain.models.form.customform;

import fr.siamois.domain.models.form.customfield.CustomField;
import lombok.Data;


@Data
public class CustomCol {

    private boolean readOnly = false;
    private CustomField field;
    private String className;

}
