package fr.siamois.models.history;

import fr.siamois.models.ReadOnlyEntity;
import fr.siamois.models.recordingunit.RecordingUnitParent;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Entity
@Table(name = "history_recording_unit")
@EntityListeners(ReadOnlyEntity.class)
public class RecordingUnitHist extends RecordingUnitParent {

    @Id
    @Column(name = "history_id")
    private Long id;

    @Column(name = "update_type", length = 10)
    @Enumerated(EnumType.STRING)
    private HistoryUpdateType updateType;

}
