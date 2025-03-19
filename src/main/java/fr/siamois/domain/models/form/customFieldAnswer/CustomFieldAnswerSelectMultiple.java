package fr.siamois.domain.models.form.customFieldAnswer;

import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("SELECT_MULTIPLE")
@Table(name = "custom_field_answer")
public class CustomFieldAnswerSelectMultiple extends CustomFieldAnswer {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "custom_field_answer_concept_answers",
            joinColumns = {
                    @JoinColumn(name = "fk_field_id", referencedColumnName = "fk_field_id"),
                    @JoinColumn(name = "fk_form_response", referencedColumnName = "fk_form_response")
            },
            inverseJoinColumns = { @JoinColumn(name = "fk_concept") }
    )
    private List<Concept> concepts = new ArrayList<>();

    /**
     * Adds a concept to the list if it doesn't already exist
     *
     * @param concept The concept to add
     */
    public void addConcept(Concept concept) {

        if (!concepts.contains(concept)) {
            concepts.add(concept);
        }
    }

    /**
     * Removes a concept from the list if it exists
     *
     * @param concept The concept to remove
     */
    public void removeConcept(Concept concept) {
        concepts.remove(concept);
    }

}
