package fr.siamois.domain.models.spatialunit;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class SpatialUnitParent extends TraceableEntity {

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    protected String name;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_ark_id")
    protected Ark ark;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_concept_category_id")
    protected Concept category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_parent_action_unit_id")
    protected ActionUnit parentActionUnit;

}
