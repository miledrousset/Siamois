package fr.siamois.domain.models.form.customform;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CustomRow implements Serializable {

    private List<CustomCol> columns;
}
