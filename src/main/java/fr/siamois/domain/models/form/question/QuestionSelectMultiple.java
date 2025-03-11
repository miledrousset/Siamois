package fr.siamois.domain.models.form.question;

import fr.siamois.converter.ConceptListConverter;
import fr.siamois.domain.models.form.question.options.SelectMultipleOptions;
import fr.siamois.domain.models.vocabulary.Concept;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("SELECT_MULTIPLE")
@Table(name = "question")
public class QuestionSelectMultiple extends Question {

    @Column(name = "options", columnDefinition = "jsonb")
    @Convert(converter = ConceptListConverter.class)
    private List<Concept> concepts;

}
