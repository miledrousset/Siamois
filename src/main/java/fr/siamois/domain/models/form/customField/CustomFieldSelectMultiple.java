package fr.siamois.domain.models.form.customField;

import fr.siamois.converter.ConceptListConverter;
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
@Table(name = "custom_field")
public class CustomFieldSelectMultiple extends CustomField {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(

            name = "custom_field_choices",
            joinColumns = { @JoinColumn(name = "fk_custom_field") },
            inverseJoinColumns = { @JoinColumn(name = "fk_concept") }

    )
    private List<Concept> concepts = new ArrayList<>();





}
