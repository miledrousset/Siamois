package fr.siamois.domain.models.form.customField;

import fr.siamois.converter.ConceptListConverter;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@DiscriminatorValue("SELECT_MULTIPLE")
@Table(name = "custom_field")
public class CustomFieldSelectMultiple extends CustomField {

    @Column(name = "options", columnDefinition = "jsonb")
    @Convert(converter = ConceptListConverter.class)
    private List<Concept> concepts;

}
