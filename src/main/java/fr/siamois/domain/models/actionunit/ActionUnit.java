package fr.siamois.domain.models.actionunit;

import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.FieldCode;
import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.models.exceptions.institution.NullInstitutionIdentifier;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "action_unit")
public class ActionUnit extends ActionUnitParent implements ArkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_unit_id", nullable = false)
    private Long id;

    @OneToMany
    @JoinTable(
            name = "action_unit_document",
            joinColumns = { @JoinColumn(name = "fk_action_unit_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_document_id") }
    )
    private Set<Document> documents = new HashSet<>();

    @OneToMany(fetch= FetchType.EAGER, mappedBy = "pk.actionUnit")
    private Set<ActionUnitFormMapping> formsAvailable = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "action_action_code",
            joinColumns = { @JoinColumn(name = "fk_action_id") },
            inverseJoinColumns = { @JoinColumn(name = "fk_action_code_id") }
    )
    private Set<ActionCode> secondaryActionCodes = new HashSet<>();


    @ManyToMany
    @JoinTable(
            name="action_hierarchy",
            joinColumns = { @JoinColumn(name = "fk_parent_id") },
            inverseJoinColumns = { @JoinColumn(name = "fk_child_id") }
    )
    private Set<ActionUnit> children = new HashSet<>();

    @ManyToMany(mappedBy = "children")
    private Set<ActionUnit> parents = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name="action_unit_spatial_context",
            joinColumns = { @JoinColumn(name = "fk_action_unit_id") },
            inverseJoinColumns = { @JoinColumn(name = "fk_spatial_unit_id") }
    )
    private Set<SpatialUnit> spatialContext = new HashSet<>();

    @FieldCode
    public static final String TYPE_FIELD_CODE = "SIAAU.TYPE";

    public String displayFullIdentifier() {
        if(getFullIdentifier() == null) {
            if(getCreatedByInstitution().getIdentifier() == null) {
                throw new NullInstitutionIdentifier("Institution identifier must be set");
            }
            return getCreatedByInstitution().getIdentifier() + "-" + (getIdentifier() == null ? '?' : getIdentifier());
        }
        else {
            return getFullIdentifier();
        }
    }

}