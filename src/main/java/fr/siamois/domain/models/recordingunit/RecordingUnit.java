package fr.siamois.domain.models.recordingunit;


import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.FieldCode;
import fr.siamois.domain.models.ReferencableEntity;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.exceptions.actionunit.NullActionUnitIdentifierException;
import fr.siamois.domain.models.exceptions.institution.NullInstitutionIdentifier;
import fr.siamois.domain.models.form.customformresponse.CustomFormResponse;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "recording_unit")
public class RecordingUnit extends RecordingUnitParent implements ArkEntity, ReferencableEntity {

    public RecordingUnit() {

    }

    public RecordingUnit(RecordingUnit recordingUnit) {
        setType(recordingUnit.getType());
        setActionUnit(recordingUnit.getActionUnit());
        setSecondaryType(recordingUnit.getSecondaryType());
        setSize(recordingUnit.getSize());
        setAltitude(recordingUnit.getAltitude());
        setCreatedByInstitution(recordingUnit.getCreatedByInstitution());
        setAuthor(recordingUnit.getAuthor());
        setAuthors(recordingUnit.getAuthors());
        setExcavators(recordingUnit.getExcavators());
        setSpatialUnit(recordingUnit.getSpatialUnit());
    }

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


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "recording_unit_authors",
            joinColumns = @JoinColumn(name = "fk_recording_unit_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_person_id"))
    private List<Person> authors = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "recording_unit_excavators",
            joinColumns = @JoinColumn(name = "fk_recording_unit_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_person_id"))
    private List<Person> excavators = new ArrayList<>();


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
    @Override
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

    @Transient
    @JsonIgnore
    public List<String> getBindableFieldNames() {
        return List.of("creationTime", "startDate", "endDate", "fullIdentifier", "authors",
                "excavators", "type", "secondaryType", "thirdType","actionUnit","spatialUnit");
    }
}