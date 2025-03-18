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

    @Column(name = "answer", columnDefinition = "jsonb")
    @Convert(converter = ConceptListConverter.class)
    @ColumnTransformer(write = "?::jsonb")
    private List<Concept> concepts;

}
