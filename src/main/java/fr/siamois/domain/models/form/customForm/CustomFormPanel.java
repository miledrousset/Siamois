package fr.siamois.domain.models.form.customForm;

import fr.siamois.domain.models.form.customField.CustomField;
import lombok.Data;

import java.util.List;

@Data
public class CustomFormPanel {

    private String className;
    private List<CustomField> fields;

}
