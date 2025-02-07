package fr.siamois.models.recordingunit;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
@Data
public class RecordingUnitSize implements Serializable {

    private BigDecimal sizeLength;
    private BigDecimal sizeWidth;
    private BigDecimal sizeThickness;
    private String sizeUnit;

}
