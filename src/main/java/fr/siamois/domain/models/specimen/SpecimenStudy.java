package fr.siamois.domain.models.specimen;


import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.FieldCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "specimen_study")
public class SpecimenStudy extends SpecimenStudyParent implements ArkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specimen_study_id", nullable = false)
    private Long id;

    @FieldCode
    public static final String STUDY_TYPE_FIELD_CODE = "SIASS.METHOD";

}