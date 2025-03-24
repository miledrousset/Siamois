package fr.siamois.domain.models.recordingunit;


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
@Table(name = "recording_unit_study")
public class RecordingUnitStudy extends RecordingUnityStudyParent implements ArkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recording_unit_study_id", nullable = false)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ru_study_document",
            joinColumns = @JoinColumn(name = "fk_document_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_ru_study_id")
    )
    private transient Set<Document> documents = new HashSet<>();

    @FieldCode
    public static final String TYPE_FIELD = "SIASRU.TYPE";

}