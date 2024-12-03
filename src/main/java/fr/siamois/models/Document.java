package fr.siamois.models;

import fr.siamois.models.ark.Ark;

import fr.siamois.models.auth.Person;
import fr.siamois.models.vocabulary.Concept;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "siamois_document", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "siamois_document_fk_ark_id_key", columnNames = {"fk_ark_id"})
})
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id", nullable = false)
    private Long id;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_author_id")
    private Person author;

    @Column(name = "url", length = Integer.MAX_VALUE)
    private String url;

}