package fr.siamois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.locationtech.jts.geom.Polygon;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "recording_unit")
public class RecordingUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recording_unit_id", nullable = false)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    private Ark ark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_type")
    private Concept type;

    @Column(name = "recording_unit_date")
    private OffsetDateTime recordingUnitDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_action_unit_id", nullable = false)
    private ActionUnit actionUnit;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_spatial_unit_id", nullable = false)
    private SpatialUnit spatialUnit;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_author_id", nullable = false)
    private Person author;

    //@Column(name = "spatial_extent", columnDefinition = "geometry not null")
    //private Polygon spatialExtent;

}