package fr.siamois.domain.models.form.customform;

import fr.siamois.domain.models.form.customfield.CustomField;
import lombok.Data;

import java.io.Serializable;


@Data
public class CustomCol implements Serializable {

    private boolean readOnly = false;
    private CustomField field;
    private String className;

}
