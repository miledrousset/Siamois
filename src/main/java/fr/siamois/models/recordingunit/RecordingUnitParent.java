package fr.siamois.models.recordingunit;

import fr.siamois.models.ActionUnit;
import fr.siamois.models.TraceableEntity;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.auth.Person;
import fr.siamois.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

/**
 * The common attributes of the history recording unit table and the real recording table.
 *
 * @author Julien Linget
 */
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class RecordingUnitParent extends TraceableEntity {

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "fk_ark_id")
    protected Ark ark;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_type")
    protected Concept type;

    @Column(name = "start_date")
    protected OffsetDateTime startDate;

    @Column(name = "end_date")
    protected OffsetDateTime endDate;

    @Column(name = "serial_identifier")
    protected Integer serial_id;

    @Column(name = "description")
    protected String description;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_action_unit_id", nullable = false)
    protected ActionUnit actionUnit;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_excavator_id")
    protected Person excavator;

    @Embedded
    protected RecordingUnitSize size;

    @Embedded
    protected RecordingUnitAltimetry altitude;

}
