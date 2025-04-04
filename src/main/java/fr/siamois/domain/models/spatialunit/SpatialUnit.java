package fr.siamois.domain.models.spatialunit;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.FieldCode;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.form.customfield.CustomField;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "spatial_unit")
@SQLRestriction("fk_parent_action_unit_id IS NULL")
public class SpatialUnit extends SpatialUnitGeneric implements ArkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spatial_unit_id", nullable = false)
    private Long id;

    @FieldCode
    public static final String CATEGORY_FIELD_CODE = "SIASU.TYPE";

    @OneToMany
    @JoinTable(
            name = "spatial_unit_document",
            joinColumns = { @JoinColumn(name = "fk_spatial_unit_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_document_id") }
    )
    private Set<Document> documents = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name="spatial_hierarchy",
            joinColumns = { @JoinColumn(name = "fk_parent_id") },
            inverseJoinColumns = { @JoinColumn(name = "fk_child_id") }
    )
    private Set<SpatialUnit> children = new HashSet<>();

    @ManyToMany(mappedBy = "children")
    private Set<SpatialUnit> parents = new HashSet<>();



}