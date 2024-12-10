package fr.siamois.models.recordingunit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.math.BigDecimal;

@Embeddable
@Data
public class RecordingUnitAltimetry {

    private BigDecimal altitude_sup_plus;
    private BigDecimal altitude_sup_minus;
    private BigDecimal altitude_inf_plus;
    private BigDecimal altitude_inf_minus;
    private String altitude_unit;

}
