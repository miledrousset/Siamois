package fr.siamois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "specimen")
public class Specimen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specimen_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_recording_unit_id")
    private RecordingUnit recordingUnit;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    private Ark ark;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_specimen_category")
    private Concept specimenCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_collection_method")
    private Concept collectionMethod;

    @Column(name = "collection_date")
    private OffsetDateTime collectionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_action_unit_id")
    private ActionUnit actionUnit;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_author_id", nullable = false)
    private Person author;

    @Column(name = "coordinates", columnDefinition = "geometry not null")
    private Point coordinates;

}