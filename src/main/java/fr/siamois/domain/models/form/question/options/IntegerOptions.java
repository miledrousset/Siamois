package fr.siamois.domain.models.form.question.options;


import lombok.Data;

import java.io.Serializable;

@Data
public class IntegerOptions implements Serializable {

    private Float min;
    private Float max;

}
