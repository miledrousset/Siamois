package fr.siamois.domain.models.permission;

import fr.siamois.domain.models.spatialunit.SpatialUnit;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class SpatialUnitPermission extends EntityPermission {

    @JoinColumn(name = "fk_spatial_unit_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private SpatialUnit spatialUnit;

}
