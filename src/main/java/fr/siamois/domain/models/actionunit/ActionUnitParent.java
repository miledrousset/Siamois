package fr.siamois.domain.models.actionunit;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class ActionUnitParent extends TraceableEntity {

    protected ActionUnitParent() {
        this.maxRecordingUnitCode = Integer.MAX_VALUE;
        this.minRecordingUnitCode = 1;
    }

    @Column(name = "begin_date")
    protected OffsetDateTime beginDate;
    
    @Column(name = "end_date")
    protected OffsetDateTime endDate;

    @NotNull
    @Column(name = "name", nullable = false)
    protected String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_type")
    protected Concept type;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "fk_ark_id")
    protected Ark ark;


    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "fk_primary_action_code")
    protected ActionCode primaryActionCode;

    @NotNull
    @Column(name="identifier")
    protected String identifier;

    @NotNull
    @Column(name="full_identifier")
    protected String fullIdentifier;

    @NotNull
    @Column(name="max_recording_unit_code", nullable = false)
    protected Integer maxRecordingUnitCode;

    @NotNull
    @Column(name="min_recording_unit_code")
    protected Integer minRecordingUnitCode;






}
