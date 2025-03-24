package fr.siamois.domain.models.specimen;

import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "specimen_study_typology", schema = "public")
public class SpecimenStudyTypology {
    @EmbeddedId
    private SpecimenStudyTypologyId id;

    @MapsId("fkSpecimenStudyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_specimen_study_id", nullable = false)
    private SpecimenStudy fkSpecimenStudy;

    @MapsId("fkTypologyConceptId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_typology_concept_id", nullable = false)
    private Concept fkTypologyConcept;

    @MapsId("fkQuestionConceptId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_question_concept_id", nullable = false)
    private Concept fkQuestionConcept;

    @Embeddable
    @Getter
    public static class SpecimenStudyTypologyId {
        @NotNull
        @Column(name = "fk_specimen_study_id", nullable = false)
        private Long fkSpecimenStudyId;

        @NotNull
        @Column(name = "fk_typology_concept_id", nullable = false)
        private Long fkTypologyConceptId;

        @NotNull
        @Column(name = "fk_question_concept_id", nullable = false)
        private Long fkQuestionConceptId;
    }

}