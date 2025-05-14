package fr.siamois.domain.models.permission;

import fr.siamois.domain.models.recordingunit.RecordingUnit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("RECORDING_UNIT")
public class RecordingUnitPermission extends EntityPermission {

    @JoinColumn(name = "fk_recording_unit_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private RecordingUnit recordingUnit;

}
