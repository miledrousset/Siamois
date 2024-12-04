package fr.siamois.models.recordingunit;


import fr.siamois.models.ActionUnit;
import fr.siamois.models.SpatialUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.auth.Person;
import fr.siamois.models.vocabulary.Concept;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade=CascadeType.PERSIST)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    private Ark ark;

    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.PERSIST)
    @JoinColumn(name = "fk_type")
    private Concept type;

    @Column(name = "recording_unit_date")
    private OffsetDateTime recordingUnitDate;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_action_unit_id", nullable = false)
    private ActionUnit actionUnit;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade=CascadeType.PERSIST)
    @JoinColumn(name = "fk_author_id", nullable = false)
    private Person author;

    // TODO : also add "fouilleur"

    //@Column(name = "spatial_extent", columnDefinition = "geometry not null")
    //private Polygon spatialExtent;

}