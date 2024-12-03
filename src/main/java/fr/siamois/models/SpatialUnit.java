package fr.siamois.models;

import fr.siamois.models.ark.Ark;
import fr.siamois.models.vocabulary.Concept;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "spatial_unit")
public class SpatialUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spatial_unit_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    private Ark ark;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_concept_category_id")
    private Concept category;

//    @Column(name = "spatial_extent", columnDefinition = "geometry not null")
//    private Polygon spatialExtent;

    public static final String CATEGORY_FIELD_CODE = "spatialUnit.category.collection";

}