package fr.siamois.models.history;

import fr.siamois.models.recordingunit.RecordingUnitParent;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@Table(name = "history_recording_unit")
@Immutable
public class RecordingUnitHist extends RecordingUnitParent {

    @Id
    @Column(name = "history_id")
    private Long id;

    @Column(name = "recording_unit_id", nullable = false)
    private Long tableId;

    @Column(name = "update_type", length = 10)
    @Enumerated(EnumType.STRING)
    private HistoryUpdateType updateType;

}
