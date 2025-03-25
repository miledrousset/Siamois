package fr.siamois.domain.models.recordingunit;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;
import java.util.Objects;


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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_secondary_type")
    protected Concept secondaryType;


    @Column(name = "start_date")
    protected OffsetDateTime startDate;

    @Column(name = "end_date")
    protected OffsetDateTime endDate;

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

    @NotNull
    @Column(name = "identifier")
    protected Integer identifier;

    @NotNull
    @Column(name = "full_identifier")
    protected String fullIdentifier;

    @Embedded
    protected RecordingUnitSize size;

    @Embedded
    protected RecordingUnitAltimetry altitude;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordingUnit that = (RecordingUnit) o;
        return Objects.equals(fullIdentifier, that.fullIdentifier);  // Compare based on RecordingUnit
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullIdentifier);  // Hash based on RecordingUnit
    }


}
