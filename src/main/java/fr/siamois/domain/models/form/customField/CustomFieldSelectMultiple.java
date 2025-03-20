package fr.siamois.domain.models.form.customField;

import fr.siamois.converter.ConceptListConverter;
import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
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
    private Set<Concept> concepts = new HashSet<>();


}
