package fr.siamois.domain.models.form.customform;

import fr.siamois.domain.models.form.customfield.CustomField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CustomFormPanel implements Serializable {

    private String className;
    private String name;
    private List<CustomField> fields;

}
