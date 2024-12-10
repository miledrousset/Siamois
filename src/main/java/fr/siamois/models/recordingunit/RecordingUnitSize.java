package fr.siamois.models.recordingunit;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.math.BigDecimal;

@Embeddable
@Data
public class RecordingUnitSize {

    private BigDecimal size_length;
    private BigDecimal size_width;
    private BigDecimal size_thickness;
    private String size_unit;

}
