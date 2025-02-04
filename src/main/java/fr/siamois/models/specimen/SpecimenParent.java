package fr.siamois.models.specimen;

import fr.siamois.models.TraceableEntity;
import fr.siamois.models.actionunit.ActionUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.recordingunit.RecordingUnit;
import fr.siamois.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class SpecimenParent extends TraceableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_recording_unit_id")
    protected RecordingUnit recordingUnit;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_ark_id")
    protected Ark ark;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_specimen_category")
    protected Concept specimenCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_collection_method")
    protected Concept collectionMethod;

    @Column(name = "collection_date")
    protected OffsetDateTime collectionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_action_unit_id")
    protected ActionUnit actionUnit;

}
