package fr.siamois.domain.models.spatialunit;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.FieldCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "spatial_unit")
@SQLRestriction("fk_parent_action_unit_id IS NULL")
public class SpatialUnit extends SpatialUnitGeneric implements ArkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spatial_unit_id", nullable = false)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "spatial_hierarchy",
            joinColumns = @JoinColumn(name = "fk_parent_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_child_id")
    )
    private List<SpatialUnit> parents;

    @ManyToMany(mappedBy = "parents", fetch = FetchType.LAZY)
    private List<SpatialUnit> childs;

    @FieldCode
    public static final String CATEGORY_FIELD_CODE = "SIASU.TYPE";

}