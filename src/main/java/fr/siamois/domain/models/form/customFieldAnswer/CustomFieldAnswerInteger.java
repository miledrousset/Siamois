package fr.siamois.domain.models.form.customFieldAnswer;

import fr.siamois.converter.IntegerToJsonConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("INTEGER")
@Table(name = "custom_field_answer")
public class CustomFieldAnswerInteger extends CustomFieldAnswer {

    @Column(name = "answer", columnDefinition = "jsonb")
    @Convert(converter = IntegerToJsonConverter.class)
    private Integer value;

}
