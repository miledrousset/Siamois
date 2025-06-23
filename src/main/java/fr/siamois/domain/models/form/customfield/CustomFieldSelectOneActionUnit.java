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
@DiscriminatorValue("SELECT_ONE_ACTION_UNIT")
@Table(name = "custom_field")
public class CustomFieldSelectOneActionUnit extends CustomField {
    public static class Builder {

        private final CustomFieldSelectOneActionUnit field = new  CustomFieldSelectOneActionUnit();

        public CustomFieldSelectOneActionUnit.Builder label(String label) {
            field.setLabel(label);
            return this;
        }

        public CustomFieldSelectOneActionUnit.Builder isSystemField(Boolean isSystemField) {
            field.setIsSystemField(isSystemField);
            return this;
        }

        public CustomFieldSelectOneActionUnit.Builder concept(Concept concept) {
            field.setConcept(concept);
            return this;
        }

        public CustomFieldSelectOneActionUnit.Builder valueBinding(String valueBinding) {
            field.setValueBinding(valueBinding);
            return this;
        }

        public CustomFieldSelectOneActionUnit build() {
            return field;
        }
    }

}
