package fr.siamois.domain.models;

import fr.siamois.domain.models.auth.Person;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "field", schema = "public")
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "field_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "field_code", nullable = false, length = Integer.MAX_VALUE)
    private String fieldCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id")
    private Person user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_parent_concept_id")
    private Concept parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_question_concept_id")
    private Concept question;

}