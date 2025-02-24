package fr.siamois.models.recordingunit;

import fr.siamois.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "stratigraphic_relationship")
@Getter
@Setter
public class StratigraphicRelationship {

    @EmbeddedId
    private StratigraphicRelationshipId id;

    @NotNull
    @MapsId("unit1Id") // Maps to primary key in composite key
    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_recording_unit_1_id", nullable = false)
    private RecordingUnit unit1;

    @ManyToOne
    @MapsId("unit2Id")
    @JoinColumn(name = "fk_recording_unit_2_id", nullable = false)
    private RecordingUnit unit2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_relationship_concept_id")
    private Concept type;
}
