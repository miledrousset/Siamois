package fr.siamois.models.spatialunit;

import fr.siamois.models.FieldCode;
import fr.siamois.models.actionunit.ActionUnit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLRestriction;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "spatial_unit")
@SQLRestriction("fk_parent_action_unit_id IS NOT NULL")
public class ExcavationUnit extends SpatialUnitGeneric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spatial_unit_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_parent_action_unit_id", nullable = false)
    private ActionUnit parentActionUnit;

//    @FieldCode
//    public static final String CATEGORY_FIELD_CODE = "SIAEU.TYPE";

}