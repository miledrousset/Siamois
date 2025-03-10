package fr.siamois.domain.models.form.question;

import jakarta.persistence.DiscriminatorValue;
import lombok.Data;

import java.io.Serializable;

@Data
@DiscriminatorValue("INTEGER")
public class IntegerOptions implements Serializable {

    private Float min;
    private Float max;

}
