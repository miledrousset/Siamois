package fr.siamois.domain.models.form.customFieldAnswer;

import fr.siamois.converter.ConceptListConverter;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("INTEGER")
@Table(name = "custom_field_answer")
public class CustomFieldAnswerInteger extends CustomFieldAnswer {

    @Column(name = "answer", columnDefinition = "jsonb")
    private Integer value;

}
