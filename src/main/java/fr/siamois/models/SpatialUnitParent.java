package fr.siamois.models;

import fr.siamois.models.ark.Ark;
import fr.siamois.models.vocabulary.Concept;
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
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    protected Ark ark;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_concept_category_id")
    protected Concept category;

}
