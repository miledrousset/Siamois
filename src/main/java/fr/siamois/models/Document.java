package fr.siamois.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "siamois_document", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "siamois_document_fk_ark_id_key", columnNames = {"fk_ark_id"})
})
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id", nullable = false)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_ark_id", nullable = false)
    private Ark ark;

    @Column(name = "title", length = Integer.MAX_VALUE)
    private String title;

    @Column(name = "doc_description", length = Integer.MAX_VALUE)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_nature")
    private Concept nature;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_scale")
    private Concept scale;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_format")
    private Concept format;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_parent")
    private Document parent;

    @Column(name = "url", length = Integer.MAX_VALUE)
    private String url;

}