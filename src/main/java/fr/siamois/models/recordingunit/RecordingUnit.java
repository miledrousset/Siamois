package fr.siamois.models.recordingunit;


import fr.siamois.models.ArkEntity;
import fr.siamois.models.FieldCode;
import fr.siamois.models.exceptions.NullActionUnitIdentifier;
import fr.siamois.models.exceptions.NullInstitutionIdentifier;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "recording_unit")
public class RecordingUnit extends RecordingUnitParent implements ArkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recording_unit_id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "unit1", fetch = FetchType.LAZY)
    private transient Set<StratigraphicRelationship> relationshipsAsUnit1 = new HashSet<>();

    @OneToMany(mappedBy = "unit2", fetch = FetchType.LAZY)
    private transient Set<StratigraphicRelationship> relationshipsAsUnit2 = new HashSet<>();

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;  // Same reference
        if (obj == null || getClass() != obj.getClass()) return false;  // Different types

        RecordingUnit that = (RecordingUnit) obj;
        return Objects.equals(id, that.id);  // Compare IDs (null-safe)
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);  // Generate hash based on ID
    }

}