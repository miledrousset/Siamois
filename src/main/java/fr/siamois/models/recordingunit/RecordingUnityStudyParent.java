package fr.siamois.models.recordingunit;

import fr.siamois.models.TraceableEntity;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class RecordingUnityStudyParent extends TraceableEntity {

    @Column(name = "study_date")
    protected OffsetDateTime studyDate;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    protected Ark ark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_method")
    protected Concept method;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_action_unit_id")
    protected ActionUnit actionUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_recording_unit_id")
    protected RecordingUnit recordingUnit;

}
