package fr.siamois.models.recordingunit;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "recording_unit_study")
public class RecordingUnitStudy extends RecordingUnityStudyParent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recording_unit_study_id", nullable = false)
    private Long id;

}