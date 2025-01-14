package fr.siamois.models;

import fr.siamois.models.ark.Ark;
import fr.siamois.models.auth.Person;
import fr.siamois.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "siamois_document", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "siamois_document_fk_ark_id_key", columnNames = {"fk_ark_id"})
})
public class Document extends DocumentParent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id", nullable = false)
    private Long id;

}