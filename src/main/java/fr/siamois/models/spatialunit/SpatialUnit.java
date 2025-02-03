package fr.siamois.models.spatialunit;

import fr.siamois.models.FieldCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "spatial_unit")
public class SpatialUnit extends SpatialUnitGeneric {

    @FieldCode
    public static final String CATEGORY_FIELD_CODE = "SIASU.TYPE";

}