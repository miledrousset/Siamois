package fr.siamois.domain.models.form.customfield;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
@Entity
@DiscriminatorValue("SELECT_ONE_CONCEPT_FROM_CHILDREN_OF_CONCEPT")
@Table(name = "custom_field")
public class CustomFieldSelectOneConceptFromChildrenOfConcept extends CustomField {

    @Transient
    private CustomField parentField ; // field that has the parent answer in the form

    private String iconClass ;
    private String styleClass ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomFieldSelectOneConceptFromChildrenOfConcept that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(parentField, that.parentField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parentField);
    }


}
