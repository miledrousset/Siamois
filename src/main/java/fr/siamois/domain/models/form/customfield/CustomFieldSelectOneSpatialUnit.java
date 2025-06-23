package fr.siamois.domain.models.form.customfield;

import fr.siamois.domain.models.vocabulary.Concept;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@DiscriminatorValue("SELECT_ONE_SPATIAL_UNIT")
@Table(name = "custom_field")
public class CustomFieldSelectOneSpatialUnit extends CustomField {
    public static class Builder {

        private final CustomFieldSelectOneSpatialUnit field = new  CustomFieldSelectOneSpatialUnit();

        public CustomFieldSelectOneSpatialUnit.Builder label(String label) {
            field.setLabel(label);
            return this;
        }

        public CustomFieldSelectOneSpatialUnit.Builder isSystemField(Boolean isSystemField) {
            field.setIsSystemField(isSystemField);
            return this;
        }

        public CustomFieldSelectOneSpatialUnit.Builder concept(Concept concept) {
            field.setConcept(concept);
            return this;
        }

        public CustomFieldSelectOneSpatialUnit.Builder valueBinding(String valueBinding) {
            field.setValueBinding(valueBinding);
            return this;
        }

        public CustomFieldSelectOneSpatialUnit build() {
            return field;
        }
    }

}
