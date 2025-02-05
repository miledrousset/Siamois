package fr.siamois.models.spatialunit;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DiscriminatorFormula;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "spatial_unit")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("CASE WHEN fk_parent_action_unit_id IS NOT NULL THEN 'ExcavationUnit' ELSE 'SpatialUnit' END")
public abstract class SpatialUnitGeneric extends SpatialUnitParent {


}