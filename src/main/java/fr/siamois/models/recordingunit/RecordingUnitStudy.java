package fr.siamois.models.recordingunit;


import fr.siamois.models.ActionUnit;
import fr.siamois.models.ark.Ark;
import fr.siamois.models.auth.Person;
import fr.siamois.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "recording_unit_study")
public class RecordingUnitStudy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recording_unit_study_id", nullable = false)
    private Long id;

    @Column(name = "study_date")
    private OffsetDateTime studyDate;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    private Ark ark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_method")
    private Concept method;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_action_unit_id")
    private ActionUnit actionUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_recording_unit_id")
    private RecordingUnit recordingUnit;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_author_id", nullable = false)
    private Person author;

}