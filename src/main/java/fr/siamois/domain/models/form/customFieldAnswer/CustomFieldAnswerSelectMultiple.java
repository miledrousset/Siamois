package fr.siamois.domain.models.form.customFieldAnswer;

import fr.siamois.converter.ConceptListConverter;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.ColumnTransformer;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("SELECT_MULTIPLE")
@Table(name = "custom_field_answer")
public class CustomFieldAnswerSelectMultiple extends CustomFieldAnswer {

    @ManyToMany
    @JoinTable(
            name = "custom_field_answer_concept_answers",
            joinColumns = {
                    @JoinColumn(name = "fk_field_id", referencedColumnName = "fk_field_id"),
                    @JoinColumn(name = "fk_form_response", referencedColumnName = "fk_form_response")
            },
            inverseJoinColumns = { @JoinColumn(name = "fk_concept") }
    )
    private List<Concept> concepts;

}
