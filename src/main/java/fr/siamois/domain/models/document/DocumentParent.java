package fr.siamois.domain.models.document;

import fr.siamois.domain.models.TraceableEntity;
import fr.siamois.domain.models.ark.Ark;
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
    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "fk_ark_id", nullable = true)
    protected Ark ark;

    @Column(name = "title", length = MAX_FILE_NAME_LENGTH)
    protected String title;

    @Column(name = "doc_description", length = Integer.MAX_VALUE)
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

    @Column(name = "file_name", length = MAX_FILE_NAME_LENGTH)
    protected String fileName;

    @Column(name = "mime_type", length = Integer.MAX_VALUE)
    protected String mimeType;

    protected Long size;
    @Column(name = "file_internal_code", length = FILE_INTERNAL_CODE_LENGTH, unique = true)
    protected String fileCode;

    @Column(name = "md5_sum")
    protected String md5Sum;

    public static final int MAX_FILE_NAME_LENGTH = 255;
    public static final int FILE_INTERNAL_CODE_LENGTH = 10;

}
