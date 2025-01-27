package fr.siamois.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "spatial_unit")
public class SpatialUnit extends SpatialUnitParent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spatial_unit_id", nullable = false)
    private Long id;

    public static final String CATEGORY_FIELD_CODE = "SIASU.TYPE";

}