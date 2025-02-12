package fr.siamois.models.recordingunit;


import fr.siamois.models.FieldCode;
import fr.siamois.models.Institution;
import fr.siamois.models.exceptions.NullActionUnitIdentifier;
import fr.siamois.models.exceptions.NullInstitutionIdentifier;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "recording_unit")
public class RecordingUnit extends RecordingUnitParent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recording_unit_id", nullable = false)
    private Long id;

    @FieldCode
    public static final String TYPE_FIELD_CODE = "SIARU.TYPE";

    @FieldCode
    public static final String METHOD_FIELD_CODE = "SIARU.TYPE";

    @FieldCode
    public static final String STRATI_FIELD_CODE = "SIARU.STRATI";

    public String displayFullIdentifier() {
        if(getFullIdentifier() == null) {
            if(getCreatedByInstitution().getIdentifier() == null) {
                throw new NullInstitutionIdentifier("Institution identifier must be set");
            }
            if(getActionUnit().getIdentifier() == null) {
                throw new NullActionUnitIdentifier("Action identifier must be set");
            }
            return getCreatedByInstitution().getIdentifier() + "-" + getActionUnit().getIdentifier() + "-" + (getIdentifier() == null ? "?" : getIdentifier());
        }
        else {
            return getFullIdentifier();
        }
    }

}