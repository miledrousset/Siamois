package fr.siamois.domain.models.permission;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("SPATIAL_UNIT")
public class SpatialUnitPermission extends EntityPermission {

    @JoinColumn(name = "fk_spatial_unit_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private SpatialUnit spatialUnit;

}
