package fr.siamois.domain.models.recordingunit;


import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.FieldCode;
import fr.siamois.domain.models.ReferencableEntity;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.exceptions.actionunit.NullActionUnitIdentifierException;
import fr.siamois.domain.models.exceptions.institution.NullInstitutionIdentifier;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "recording_unit")
public class RecordingUnit extends RecordingUnitParent implements ArkEntity, ReferencableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recording_unit_id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "unit1", fetch = FetchType.LAZY)
    private transient Set<StratigraphicRelationship> relationshipsAsUnit1 = new HashSet<>();

    @OneToMany(mappedBy = "unit2", fetch = FetchType.LAZY)
    private transient Set<StratigraphicRelationship> relationshipsAsUnit2 = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name="recording_unit_hierarchy",
            joinColumns = { @JoinColumn(name = "fk_parent_id") },
            inverseJoinColumns = { @JoinColumn(name = "fk_child_id") }
    )
    private Set<RecordingUnit> children = new HashSet<>();

    @ManyToMany(mappedBy = "children")
    private Set<RecordingUnit> parents = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="fk_spatial_unit_id")
    private SpatialUnit spatialUnit;


    @OneToOne(
            orphanRemoval=true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinColumn(name = "fk_custom_form_response", referencedColumnName = "custom_form_response_id")
    private CustomFormResponse formResponse;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "recording_unit_document",
            joinColumns = { @JoinColumn(name = "fk_recording_unit_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_document_id") }
    )
    private Set<Document> documents = new HashSet<>();

    @FieldCode
    public static final String TYPE_FIELD_CODE = "SIARU.TYPE";

    @FieldCode
    public static final String METHOD_FIELD_CODE = "SIARU.TYPE";

    @FieldCode
    public static final String STRATI_FIELD_CODE = "SIARU.STRATI";

    // Setters/Removers
    public void setFormResponse(CustomFormResponse formResponse) {
        if (formResponse != null) {
            formResponse.setRecordingUnit(this);
        }
        this.formResponse = formResponse;
    }

    // utils
    public String displayFullIdentifier() {
        if(getFullIdentifier() == null) {
            if(getCreatedByInstitution().getIdentifier() == null) {
                throw new NullInstitutionIdentifier("Institution identifier must be set");
            }
            if(getActionUnit().getIdentifier() == null) {
                throw new NullActionUnitIdentifierException("Action identifier must be set");
            }
            return getCreatedByInstitution().getIdentifier() + "-" + getActionUnit().getIdentifier() + "-" + (getIdentifier() == null ? "?" : getIdentifier());
        }
        else {
            return getFullIdentifier();
        }
    }


    @Override
    public String getTableName() {
        return "RECORDING_UNIT";
    }

    @Override
    public String toString() {
        return String.format("Recording Unit %s", displayFullIdentifier());
    }
}