package fr.siamois.models;

import fr.siamois.models.ark.Ark;
import fr.siamois.models.vocabulary.Concept;
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

    @Column(name = "title", length = Integer.MAX_VALUE)
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

}
