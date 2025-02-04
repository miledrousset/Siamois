package fr.siamois.models.actionunit;

import fr.siamois.models.SpatialUnit;
import fr.siamois.models.TraceableEntity;
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
public abstract class ActionUnitParent extends TraceableEntity {

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

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_spatial_unit_id", nullable = false)
    protected SpatialUnit spatialUnit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_primary_action_code")
    protected ActionCode primaryActionCode;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "action_action_code",
//            joinColumns = { @JoinColumn(name = "fk_action_unit_id") },
//            inverseJoinColumns = { @JoinColumn(name = "fk_action_code_id") }
//    )
//    protected ArrayList<ActionCode> secondaryActionCodes;

}
