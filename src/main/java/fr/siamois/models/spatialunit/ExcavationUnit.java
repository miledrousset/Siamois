package fr.siamois.models.spatialunit;

import fr.siamois.models.FieldCode;
import fr.siamois.models.actionunit.ActionUnit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "spatial_unit")
public class ExcavationUnit extends SpatialUnitGeneric {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_parent_action_unit_id", nullable = false)
    private ActionUnit parentActionUnit;

    @FieldCode
    public static final String CATEGORY_FIELD_CODE = "SIAEU.TYPE";

}