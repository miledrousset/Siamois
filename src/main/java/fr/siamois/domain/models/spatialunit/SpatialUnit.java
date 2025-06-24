package fr.siamois.domain.models.spatialunit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.FieldCode;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "spatial_unit")
@SQLRestriction("fk_parent_action_unit_id IS NULL")
public class SpatialUnit extends SpatialUnitGeneric implements ArkEntity {

    @SuppressWarnings("CopyConstructorMissesField")
    public SpatialUnit (SpatialUnit spatialUnit) {
        name = spatialUnit.getName();
        ark = spatialUnit.getArk();
        category = spatialUnit.getCategory();
        geom = spatialUnit.getGeom();
        validated = spatialUnit.getValidated();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spatial_unit_id", nullable = false)
    private Long id;

    @FieldCode
    public static final String CATEGORY_FIELD_CODE = "SIASU.TYPE";

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "spatial_unit_document",
            joinColumns = { @JoinColumn(name = "fk_spatial_unit_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_document_id") }
    )
    @JsonIgnore
    private Set<Document> documents = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(
            name="spatial_hierarchy",
            joinColumns = { @JoinColumn(name = "fk_parent_id") },
            inverseJoinColumns = { @JoinColumn(name = "fk_child_id") }
    )
    private Set<SpatialUnit> children = new HashSet<>();

    @OneToMany(mappedBy="spatialUnit")
    @JsonIgnore
    private Set<RecordingUnit> recordingUnitList;

    @ManyToMany(mappedBy = "children", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<SpatialUnit> parents = new HashSet<>();

    @ManyToMany(mappedBy = "spatialContext")
    @JsonIgnore
    private Set<ActionUnit> relatedActionUnitList = new HashSet<>();

    public SpatialUnit() {

    }

    @Override
    public String toString() {
        return String.format("Spatial unit n°%s : %s", id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpatialUnit that = (SpatialUnit) o;
        return Objects.equals(id, that.id);  // Compare based on RecordingUnit
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);  // Hash based on RecordingUnit
    }

    @Transient
    public List<String> getBindableFieldNames() {
        return List.of("category", "name");
    }

}