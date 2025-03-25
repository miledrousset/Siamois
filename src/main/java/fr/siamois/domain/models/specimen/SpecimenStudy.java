package fr.siamois.domain.models.specimen;


import fr.siamois.domain.models.ArkEntity;
import fr.siamois.domain.models.FieldCode;
import fr.siamois.domain.models.document.Document;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "specimen_study")
public class SpecimenStudy extends SpecimenStudyParent implements ArkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "specimen_study_id", nullable = false)
    private Long id;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "specimenStudy")
    private Set<Document> documents = new HashSet<>();

    @FieldCode
    public static final String STUDY_TYPE_FIELD_CODE = "SIASS.METHOD";

}