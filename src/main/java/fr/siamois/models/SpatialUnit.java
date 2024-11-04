package fr.siamois.models;

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
    private Integer id;

    @NotNull
    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    private Ark ark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_concept_category_id")
    private Concept category;

    // todo spatial unit hierarchy

/*
 TODO [Reverse Engineering] create field to map the 'spatial_extent' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "spatial_extent", columnDefinition = "geometry not null")
    private Object spatialExtent;
*/
}