package fr.siamois.domain.models.form.customform;

import fr.siamois.domain.models.form.customfield.CustomField;
import lombok.Data;

import java.util.List;

@Data
public class CustomRow {

    private List<CustomCol> columns;
}
