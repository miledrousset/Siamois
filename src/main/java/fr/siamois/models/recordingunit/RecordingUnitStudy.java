package fr.siamois.models.recordingunit;


import fr.siamois.models.ArkEntity;
import fr.siamois.models.FieldCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "recording_unit_study")
public class RecordingUnitStudy extends RecordingUnityStudyParent implements ArkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recording_unit_study_id", nullable = false)
    private Long id;

    @FieldCode
    public static final String TYPE_FIELD = "SIASRU.TYPE";

}