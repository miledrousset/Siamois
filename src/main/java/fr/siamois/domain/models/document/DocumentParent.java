package fr.siamois.domain.models.document;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.actionunit.ActionUnit;
import fr.siamois.domain.models.ark.Ark;
import fr.siamois.domain.models.recordingunit.RecordingUnit;
import fr.siamois.domain.models.recordingunit.RecordingUnitStudy;
import fr.siamois.domain.models.spatialunit.SpatialUnit;
import fr.siamois.domain.models.specimen.Specimen;
import fr.siamois.domain.models.specimen.SpecimenStudy;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class DocumentParent extends TraceableEntity {

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_ark_id")
    protected Ark ark;

    @Column(name = "title", length = MAX_TITLE_LENGTH)
    protected String title;

    @Column(name = "doc_description", length = MAX_DESCRIPTION_LENGTH)
    protected String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_nature")
    protected Concept nature;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_scale")
    protected Concept scale;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_format")
    protected Concept format;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_parent")
    protected Document parent;

    @Column(name = "url", length = Integer.MAX_VALUE)
    protected String url;

    @Column(name = "file_name")
    protected String fileName;

    @Column(name = "mime_type", length = Integer.MAX_VALUE)
    protected String mimeType;

    protected Long size;
    @Column(name = "file_internal_code", length = FILE_INTERNAL_CODE_LENGTH, unique = true)
    protected String fileCode;

    @Column(name = "md5_sum")
    protected String md5Sum;

    @ManyToOne
    @JoinTable(
            name = "spatial_unit_document",
            joinColumns = { @JoinColumn(name = "fk_document_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_spatial_unit_id") }
    )
    protected SpatialUnit spatialUnit;

    @ManyToOne
    @JoinTable(
            name = "action_unit_document",
            joinColumns = { @JoinColumn(name = "fk_document_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_action_unit_id") }
    )
    protected ActionUnit actionUnit;

    @ManyToOne
    @JoinTable(
            name = "recording_unit_document",
            joinColumns = { @JoinColumn(name = "fk_document_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_recording_unit_id") }
    )
    protected RecordingUnit recordingUnit;

    @ManyToOne
    @JoinTable(
            name = "ru_study_document",
            joinColumns = { @JoinColumn(name = "fk_document_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_ru_study_id") }
    )
    protected RecordingUnitStudy recordingUnitStudy;

    @ManyToOne
    @JoinTable(
            name = "specimen_document",
            joinColumns = { @JoinColumn(name = "fk_document_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_specimen_id") }
    )
    protected Specimen specimen;

    @ManyToOne
    @JoinTable(
            name = "specimen_study_document",
            joinColumns = { @JoinColumn(name = "fk_document_id")},
            inverseJoinColumns = { @JoinColumn(name = "fk_specimen_study_id") }
    )
    protected SpecimenStudy specimenStudy;

    protected String storedFileName;

    public static final int MAX_FILE_NAME_LENGTH = 255;
    public static final int FILE_INTERNAL_CODE_LENGTH = 10;
    public static final int MAX_DESCRIPTION_LENGTH = 1024;
    public static final int MAX_TITLE_LENGTH = 50;

}
