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
@DiscriminatorValue("SELECT_MULTIPLE_PERSON")
@Table(name = "custom_field")
public class CustomFieldSelectMultiplePerson extends CustomFieldSelectPerson {

    public static class Builder {

        private final CustomFieldSelectMultiplePerson field = new  CustomFieldSelectMultiplePerson();

        public Builder label(String label) {
            field.setLabel(label);
            return this;
        }

        public Builder isSystemField(Boolean isSystemField) {
            field.setIsSystemField(isSystemField);
            return this;
        }

        public Builder concept(Concept concept) {
            field.setConcept(concept);
            return this;
        }

        public Builder valueBinding(String valueBinding) {
            field.setValueBinding(valueBinding);
            return this;
        }

        public CustomFieldSelectMultiplePerson build() {
            return field;
        }
    }

}
