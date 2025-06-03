package fr.siamois.domain.models.form.customform;

import fr.siamois.domain.models.form.customfield.CustomField;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Data
public class CustomFormPanel implements Serializable {

    private String className;
    private String name;
    private List<CustomRow> rows;
    private Boolean isSystemPanel; // define by system or user?

}
