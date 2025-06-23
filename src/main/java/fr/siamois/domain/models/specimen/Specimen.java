package fr.siamois.domain.models.specimen;


import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.FieldCode;
import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.document.Document;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "specimen")
public class Specimen extends SpecimenParent implements ArkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specimen_id", nullable = false)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "specimen_document",
            joinColumns = { @JoinColumn(name = "fk_specimen_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_document_id") }
    )
    private Set<Document> documents = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "specimen_authors",
            joinColumns = @JoinColumn(name = "fk_specimen_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_person_id"))
    private List<Person> authors;

    @ManyToMany(mappedBy = "specimens")
    private Set<SpecimenGroup> groups = new HashSet<>();

    @FieldCode
    public static final String CATEGORY_FIELD = "SIAS.CATEGORY";

    @FieldCode
    public static final String METHOD_FIELD = "SIAS.METHOD";

}